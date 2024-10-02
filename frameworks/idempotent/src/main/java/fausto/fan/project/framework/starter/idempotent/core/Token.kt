package fausto.fan.project.framework.starter.idempotent.core

import cn.hutool.core.lang.UUID
import fausto.fan.project.framework.starter.cache.DistributedCache
import fausto.fan.project.framework.starter.convention.errorcode.BaseErrorCode
import fausto.fan.project.framework.starter.convention.exception.ClientException
import fausto.fan.project.framework.starter.convention.result.Result
import fausto.fan.project.framework.starter.idempotent.config.IdempotentProperties
import fausto.fan.project.framework.starter.web.Results
import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 幂等性令牌服务接口，提供令牌创建的方法
 */
interface IdempotentTokenService: IdempotentExecuteHandler {
    /**
     * 创建幂等性令牌
     * @return 生成的令牌字符串
     */
    fun createToken(): String
}

/**
 * 幂等性令牌执行处理器，实现了幂等性执行处理逻辑和令牌服务
 * 通过分布式缓存来存储和管理令牌，以保证操作的幂等性
 */
class IdempotentTokenExecuteHandler(
    private val distributedCache: DistributedCache, // 分布式缓存，用于存储令牌
    private val idempotentProperties: IdempotentProperties // 幂等性属性配置
): AbstractIdempotentExecuteHandler(), IdempotentTokenService {

    /**
     * 构建幂等性参数包装器
     * @param joinPoint 当前的连接点对象，用于获取方法执行的上下文信息
     * @return 返回构建的IdempotentParamWrapper对象
     */
    override fun buildWrapper(joinPoint: ProceedingJoinPoint): IdempotentParamWrapper
        = IdempotentParamWrapper()

    /**
     * 创建幂等性令牌
     * 根据配置的前缀生成令牌，并将其存储到分布式缓存中
     * @return 生成的令牌字符串
     */
    override fun createToken(): String {
        val token = if(idempotentProperties.prefix.isNullOrBlank()) {
            // 如果未配置前缀，则使用默认前缀加上UUID来生成令牌
            TOKEN_PREFIX_KEY + UUID.randomUUID().toString()
        } else {
            // 如果配置了前缀，则使用配置的前缀
            idempotentProperties.prefix + UUID.randomUUID().toString()
        }
        // 将生成的令牌存储到分布式缓存中，并设置过期时间
        distributedCache.put(
            token,
            "",
            idempotentProperties.timeout ?: TOKEN_EXPIRED_TIME
        )
        return token
    }

    /**
     * 处理幂等性逻辑
     * 通过验证令牌是否存在来确定是否是重复请求，如果是重复请求则抛出异常
     * @param wrapper 包装了幂等性参数的包装器对象
     */
    override fun handler(wrapper: IdempotentParamWrapper) {
        // 获取当前的HTTP请求对象
        val request =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        // 从请求头或请求参数中获取令牌
        val token = request.getHeader(TOKEN_KEY)
            ?: request.getParameter(TOKEN_KEY)
            ?: throw ClientException(BaseErrorCode.IDEMPOTENT_TOKEN_NULL_ERROR)
        // 尝试从分布式缓存中删除令牌，判断是否为重复请求
        val tokenDelFlag: Boolean = distributedCache.delete(token) > 0
        if(!tokenDelFlag) {
            // 如果令牌未删除成功，抛出异常
            val errMsg: String = wrapper.idempotent!!.message.ifBlank {
                BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR.message()
            }
            throw ClientException(errMsg, BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR)
        }
    }

    // 伴生对象，用于存储常量
    companion object {
        const val TOKEN_KEY = "token" // 令牌的键名
        const val TOKEN_PREFIX_KEY = "idempotent:token" // 令牌的默认前缀
        const val TOKEN_EXPIRED_TIME = 6000L // 令牌的默认过期时间（毫秒）
    }
}

/**
 * 控制器类，用于处理与idempotent token相关的HTTP请求
 * 主要职责是委托给IdempotentTokenService来生成唯一的token，以确保操作的幂等性
 */
@RestController
class IdempotentTokenController(
    // 服务接口，用于执行与idempotent token相关的业务逻辑
    private val idempotentTokenService: IdempotentTokenService
) {
    /**
     * 创建并返回一个新的idempotent token
     * 该方法通过HTTP GET请求访问"/token"路径来调用
     *
     * @return 一个封装在Result中的新生成的idempotent token字符串，表示一个成功的操作结果
     */
    @GetMapping("/token")
    fun createToken(): Result<String> = Results.success(idempotentTokenService.createToken())
}
