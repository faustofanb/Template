package fausto.fan.project.framework.starter.web

import fausto.fan.project.framework.starter.convention.errorcode.BaseErrorCode
import fausto.fan.project.framework.starter.convention.exception.AbstractException
import fausto.fan.project.framework.starter.convention.result.Result

/**
 * 结果处理对象，用于简化结果的创建
 */
object Results {

    /**
     * 创建一个成功结果
     *
     * @param data 可选的数据 payload，可能为 null
     * @param T 数据的类型
     * @return 成功的 [Result] 实例，包含可能的 data 数据
     *
     * 该函数通过条件表达式判断 data 是否为 null，并根据判断结果构造 Result 实例
     * 当 data 为 null 时，仅设置结果代码为成功；否则，设置结果代码和数据
     */
    fun <T> success(data: T? = null): Result<T> {
        return when(data == null) {
            true ->  Result(code = Result.SUCCESS_CODE)
            else -> Result(code = Result.SUCCESS_CODE, data = data)
        }
    }

    /**
     * 创建一个失败结果
     *
     * @param abstractException 可选的异常实例，可能为 null
     * @param errorCode 错误代码，可能为 null
     * @param errorMessage 错误消息，可能为 null
     * @return 失败的 [Result] 实例，包含错误代码和消息
     *
     * 该函数通过条件表达式判断参数的不同情况来构造不同的失败结果
     * 当没有提供异常和错误信息时，使用默认的服务错误代码和服务消息
     * 如果提供了异常，则使用异常的错误代码和消息
     * 否则，使用提供的错误代码和消息
     */
    fun failure(
        abstractException: AbstractException? = null,
        errorCode: String? = null,
        errorMessage: String? = null
    ): Result<Void> {
        return when {
            abstractException == null && (errorCode == null && errorMessage == null) -> {
                Result(code = BaseErrorCode.SERVICE_ERROR.code(), message = BaseErrorCode.SERVICE_ERROR.message())
            }
            abstractException != null && (errorCode == null && errorMessage == null) -> {
                Result(code = abstractException.errorCode, message = abstractException.errorMessage)
            }
            else -> {
                Result(code = errorCode, message = errorMessage)
            }
        }
    }
}
