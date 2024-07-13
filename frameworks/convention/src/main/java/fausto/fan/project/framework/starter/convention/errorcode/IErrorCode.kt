package fausto.fan.project.framework.starter.convention.errorcode


/**
 * 错误代码接口，定义了错误代码和错误消息的获取方法。
 */
interface IErrorCode {

    /**
     * 获取错误代码。
     *
     * @return 错误代码字符串。
     */
    fun code(): String

    /**
     * 获取错误消息。
     *
     * @return 错误消息字符串。
     */
    fun message(): String
}

/**
 * 基础错误代码类，是一个密封类，用于定义各种错误代码。
 * 密封类允许定义一系列相关的类，这些类被认为是“封闭”的，它们可以作为有限的选项集。
 * 此类实现了IErrorCode接口，提供了错误代码和错误消息的统一管理。
 */
@Suppress("ClassName")
sealed class BaseErrorCode(private val code: String, private val message: String) : IErrorCode {

    // ========== 一级宏观错误码 客户端错误 ==========
    data object CLIENT_ERROR : BaseErrorCode("A000001", "用户端错误")

    // ========== 二级宏观错误码 用户注册错误 ==========

    data object USER_REGISTER_ERROR : BaseErrorCode("A000100", "用户注册错误")

    data object USER_NAME_VERIFY_ERROR : BaseErrorCode("A000110", "用户名校验失败")

    data object USER_NAME_EXIST_ERROR : BaseErrorCode("A000111", "用户名已存在")

    data object USER_NAME_SENSITIVE_ERROR : BaseErrorCode("A000112", "用户名包含敏感词")

    data object USER_NAME_SPECIAL_CHARACTER_ERROR : BaseErrorCode("A000113", "用户名包含特殊字符")

    data object PASSWORD_VERIFY_ERROR : BaseErrorCode("A000120", "密码校验失败")

    data object PASSWORD_SHORT_ERROR : BaseErrorCode("A000121", "密码长度不够")

    data object PHONE_VERIFY_ERROR : BaseErrorCode("A000151", "手机格式校验失败")

    // ========== 二级宏观错误码 系统请求缺少幂等Token ==========

    data object IDEMPOTENT_TOKEN_NULL_ERROR : BaseErrorCode("A000200", "幂等Token为空")

    data object IDEMPOTENT_TOKEN_DELETE_ERROR : BaseErrorCode("A000201", "幂等Token已被使用或失效")

    // ========== 一级宏观错误码 系统执行出错 ==========

    data object SERVICE_ERROR : BaseErrorCode("B000001", "系统执行出错")

    // ========== 二级宏观错误码 系统执行超时 ==========

    data object SERVICE_TIMEOUT_ERROR : BaseErrorCode("B000100", "系统执行超时")

    // ========== 一级宏观错误码 调用第三方服务出错 ==========

    data object REMOTE_ERROR : BaseErrorCode("C000001", "调用第三方服务出错")


    /**
     * 获取错误代码。
     *
     * @return 错误代码字符串。
     */
    override fun code(): String {
        return code
    }

    /**
     * 获取错误消息。
     *
     * @return 错误消息字符串。
     */
    override fun message(): String {
        return message
    }

    /**
     * 返回所有定义的错误代码实例的数组。
     *
     * @return 包含所有错误代码实例的数组。
     */
    companion object {
        fun values(): Array<BaseErrorCode> {
            return arrayOf(
                CLIENT_ERROR,
                USER_REGISTER_ERROR,
                USER_NAME_VERIFY_ERROR,
                USER_NAME_EXIST_ERROR,
                USER_NAME_SENSITIVE_ERROR,
                USER_NAME_SPECIAL_CHARACTER_ERROR,
                PASSWORD_VERIFY_ERROR,
                PASSWORD_SHORT_ERROR,
                PHONE_VERIFY_ERROR,
                IDEMPOTENT_TOKEN_NULL_ERROR,
                IDEMPOTENT_TOKEN_DELETE_ERROR,
                SERVICE_ERROR,
                SERVICE_TIMEOUT_ERROR,
                REMOTE_ERROR
            )
        }

        /**
         * 根据错误代码字符串返回对应的错误代码实例。
         *
         * @param value 错误代码字符串。
         * @return 对应的错误代码实例。
         * @throws IllegalArgumentException 如果没有找到对应的错误代码实例。
         */
        fun valueOf(value: String): BaseErrorCode {
            return when (value) {
                "CLIENT_ERROR" -> CLIENT_ERROR
                "USER_REGISTER_ERROR" -> USER_REGISTER_ERROR
                "USER_NAME_VERIFY_ERROR" -> USER_NAME_VERIFY_ERROR
                "USER_NAME_EXIST_ERROR" -> USER_NAME_EXIST_ERROR
                "USER_NAME_SENSITIVE_ERROR" -> USER_NAME_SENSITIVE_ERROR
                "USER_NAME_SPECIAL_CHARACTER_ERROR" -> USER_NAME_SPECIAL_CHARACTER_ERROR
                "PASSWORD_VERIFY_ERROR" -> PASSWORD_VERIFY_ERROR
                "PASSWORD_SHORT_ERROR" -> PASSWORD_SHORT_ERROR
                "PHONE_VERIFY_ERROR" -> PHONE_VERIFY_ERROR
                "IDEMPOTENT_TOKEN_NULL_ERROR" -> IDEMPOTENT_TOKEN_NULL_ERROR
                "IDEMPOTENT_TOKEN_DELETE_ERROR" -> IDEMPOTENT_TOKEN_DELETE_ERROR
                "SERVICE_ERROR" -> SERVICE_ERROR
                "SERVICE_TIMEOUT_ERROR" -> SERVICE_TIMEOUT_ERROR
                "REMOTE_ERROR" -> REMOTE_ERROR
                else -> throw IllegalArgumentException("No object fausto.fan.project.framework.starter.convention.errorcode.BaseErrorCode.$value")
            }

        }
    }
}
