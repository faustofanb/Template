package fausto.fan.project.framework.starter.web

import cn.hutool.core.util.StrUtil
import fausto.fan.project.framework.starter.base.Slf4j
import fausto.fan.project.framework.starter.base.Slf4j.Companion.log
import fausto.fan.project.framework.starter.convention.errorcode.BaseErrorCode
import fausto.fan.project.framework.starter.convention.exception.AbstractException
import fausto.fan.project.framework.starter.convention.result.Result
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler

// 避免重复的日志消息警告
@Suppress("LoggingSimilarMessage")
// 使用log4j2进行日志记录
@Slf4j
// 全局异常处理器类
class GlobalExceptionHandler {

    /**
     * 处理方法参数校验异常
     * 当方法参数未通过验证时触发
     *
     * @param request HTTP请求对象
     * @param ex 方法参数校验异常对象
     * @return 包含错误信息的Result对象，错误代码和错误消息
     */
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun validExceptionHandler(request: HttpServletRequest, ex: MethodArgumentNotValidException): Result<Void> {
        // 获取第一个字段错误的默认消息
        val exceptionStr = ex.bindingResult.fieldErrors.first()?.defaultMessage
            ?: StrUtil::EMPTY.get()
        // 记录错误日志
        log.error("[${request.method}] ${getUrl(request)} [ex] $exceptionStr")
        // 返回客户端错误的Result对象
        return Results.failure(errorCode = BaseErrorCode.CLIENT_ERROR.code(), errorMessage = exceptionStr)
    }

    /**
     * 处理自定义抽象异常
     * 当抛出AbstractException及其子类时触发
     *
     * @param request HTTP请求对象
     * @param ex 自定义抽象异常对象
     * @return 包含错误信息的Result对象，根据异常原因记录日志并封装错误信息
     */
    @ExceptionHandler(value = [AbstractException::class])
    fun abstractException(request: HttpServletRequest, ex: AbstractException): Result<Void> {
        // 判断异常原因是否为空
        if (ex.cause != null) {
            // 如果原因不为空，记录错误日志并返回封装的错误信息
            log.error("[${request.method}] ${request.requestURL} [ex] $ex", ex.cause)
            return Results.failure(ex)
        }
        // 如果原因为空，记录错误日志并返回封装的错误信息
        log.error("[${request.method}] ${request.requestURL} [ex] $ex")
        return Results.failure(ex)
    }

    /**
     * 处理所有未被捕获的异常和错误
     * 当抛出Throwable及其子类（除了Error外）时触发
     *
     * @param request HTTP请求对象
     * @param throwable 任意异常对象
     * @return 空的Result对象，记录异常日志
     */
    @ExceptionHandler(value = [Throwable::class])
    fun defaultErrorHandler(request: HttpServletRequest, throwable: Throwable): Result<Void> {
        // 记录异常日志，不包含具体异常信息
        log.error("[${request.method}] ${getUrl(request)}", throwable)
        // 返回通用失败的Result对象
        return Results.failure()
    }

    /**
     * 获取请求的URL，包括查询字符串
     *
     * @param request HTTP请求对象
     * @return 完整的请求URL字符串
     */
    private fun getUrl(request: HttpServletRequest): String {
        // 判断查询字符串是否为空
        if (request.queryString.isEmpty())
            return request.requestURL.toString()
        // 返回包含查询字符串的完整URL
        return request.requestURL.toString() + "?" + request.queryString
    }
}
