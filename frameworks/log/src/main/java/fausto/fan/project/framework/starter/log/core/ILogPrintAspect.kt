package fausto.fan.project.framework.starter.log.core

import cn.hutool.core.date.DateUtil
import cn.hutool.core.date.SystemClock
import com.alibaba.fastjson2.JSON
import fausto.fan.project.framework.starter.log.annotation.ILog
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.MultipartFile

/**
 * 日志打印切面类，用于在方法执行前后记录日志
 */
@Aspect
class ILogPrintAspect {

    /**
     * 环绕通知，用于拦截被ILog注解标记的方法
     *
     * @param joinPoint 切入点对象，包含方法执行的上下文信息
     * @return 方法执行的结果
     */
    @Around(
        """
        @within(fausto.fan.project.framework.starter.log.annotation.ILog)
        ||
        @annotation(fausto.fan.project.framework.starter.log.annotation.ILog)
    """
    )
    fun printLog(joinPoint: ProceedingJoinPoint): Any? {
        // 记录方法开始执行的时间
        val startTime = SystemClock.now()
        // 获取方法签名
        val methodSignature = joinPoint.signature as MethodSignature
        // 获取日志记录器
        val log = LoggerFactory.getLogger(methodSignature.declaringType)

        // 记录方法开始执行的日期时间
        val beginTime = DateUtil.now()
        // 方法执行结果初始化
        var result: Any? = null

        try {
            // 执行被拦截的方法
            result = joinPoint.proceed()
        } finally {
            // 获取目标方法
            val targetMethod = joinPoint.target.javaClass.getMethod(
                methodSignature.name,
                *methodSignature.method.parameterTypes
            )
            // 获取ILog注解实例
            val logAnnotation = targetMethod.getAnnotation(ILog::class.java)
                ?: joinPoint.target.javaClass.getAnnotation(ILog::class.java)

            // 检查是否应用了ILog注解
            if (logAnnotation != null) {
                // 构建日志打印信息
                val logPrint = ILogPrintDTO().apply {
                    this.beginTime = beginTime
                    if (logAnnotation.input) this.inputParams = buildInput(joinPoint)
                    if (logAnnotation.output) this.outputParams = result
                }
                // 初始化请求相关信息
                var methodType = ""
                var requestURI = ""
                try {
                    // 从当前线程中获取请求信息
                    val servletRequestAttributes =
                        RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
                    // 设置请求方法类型
                    methodType = servletRequestAttributes.request.method
                    // 设置请求URI
                    requestURI = servletRequestAttributes.request.requestURI
                } catch (ignored: Exception) {
                }

                // 记录方法执行完毕的日志信息
                log.info(
                    """
                    [$methodType] $requestURI ${SystemClock.now() - startTime}ms :: info: ${JSON.toJSONString(logPrint)} 
                """.trimIndent()
                )
            }
        }

        // 返回方法执行结果
        return result
    }

    /**
     * 构建输入参数的字符串表示
     *
     * @param joinPoint 切入点对象，包含方法执行的上下文信息
     * @return 输入参数的字符串数组
     */
    private fun buildInput(joinPoint: ProceedingJoinPoint): Array<Any> {
        // 获取方法的参数
        val args = joinPoint.args
        // 创建用于打印的参数数组
        val printArgs = Array<Any>(args.size) { 0 }
        // 遍历方法参数，处理特殊类型的参数
        for (i in args.indices) {
            when {
                args[i] is HttpServletRequest || args[i] is HttpServletResponse -> continue
                args[i] is ByteArray -> printArgs[i] = "byte array"
                args[i] is MultipartFile -> printArgs[i] = "file"
                else -> printArgs[i] = args[i]
            }
        }
        // 返回处理后的参数数组
        return printArgs
    }
}
