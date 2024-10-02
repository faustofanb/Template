package fausto.fan.project.framework.starter.idempotent.core

import cn.hutool.crypto.digest.DigestUtil
import fausto.fan.project.framework.starter.convention.exception.ClientException
import fausto.fan.project.frameworks.starter.user.core.UserContext
import org.aspectj.lang.ProceedingJoinPoint
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

// 定义一个接口，用于处理幂等性参数的服务
interface IdempotentParamService: IdempotentExecuteHandler

// IdempotentParamExecuteHandler类实现了抽象类AbstractIdempotentExecuteHandler和接口IdempotentParamService
// 它使用Redisson客户端来处理幂等性请求
class IdempotentParamExecuteHandler(
    private val redissonClient: RedissonClient
): AbstractIdempotentExecuteHandler(), IdempotentParamService {

    // 创建一个IdempotentParamWrapper实例，并设置锁键和JoinPoint
    override fun buildWrapper(joinPoint: ProceedingJoinPoint): IdempotentParamWrapper =
        IdempotentParamWrapper().apply {
            // 锁键由请求路径、当前用户ID和请求参数的MD5值组成
            lockKey = "idempotent:path:${getServletPath()}:currentUserId:${getCurrentUserId()}:md5:${calcArgsMd5(joinPoint)}"
            this.joinPoint = joinPoint
        }

    // 处理幂等性请求
    override fun handler(wrapper: IdempotentParamWrapper) {
        val lockKey = wrapper.lockKey
        val lock = redissonClient.getLock(lockKey)
        // 尝试获取锁，如果失败则抛出异常
        if(!lock.tryLock()) throw ClientException(wrapper.idempotent!!.message)
        // 将锁存入上下文
        IdempotentContext.put(LOCK, lock)
    }

    // 处理异常后的操作，主要是释放锁
    override fun exceptionProcessing() {
        postProcessing()
    }

    // 后处理操作，主要用于释放锁
    override fun postProcessing() {
        var lock: RLock? = null
        try {
            // 从上下文中获取锁
            lock = IdempotentContext.getKey(LOCK) as RLock
        } finally {
            // 释放锁
            lock?.unlock()
        }
    }

    // 获取当前请求的servlet路径
    private fun getServletPath(): String =
        (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes)
            .request.servletPath


    // 获取当前用户的ID，如果未登录则抛出异常
    private fun getCurrentUserId(): String =
        if (UserContext.getUserId().isNullOrEmpty())
            throw ClientException("用户ID获取失败, 请登录")
        else
            UserContext.getUserId()!!


    // 计算请求参数的MD5值
    private fun calcArgsMd5(joinPoint: ProceedingJoinPoint): String =
        DigestUtil.md5Hex(com.alibaba.fastjson2.JSON.toJSONBytes(joinPoint.args))

    companion object {
        // 定义一个常量，用于Redis锁的键前缀
        private const val LOCK: String = "lock:param:restAPI"
    }
}
