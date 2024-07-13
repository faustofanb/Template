package fausto.fan.project.framework.starter.convention.result

import java.io.Serializable

/**
 * 表示操作结果的类，包含操作的状态码、消息、数据和请求ID。
 *
 * @param<T> 结果数据的类型参数。
 * @param code 操作的状态码，可能为null。
 * @param message 操作的结果消息，可能为null。
 * @param data 操作返回的数据，可能为null。
 * @param requestId 请求的唯一标识，可能为null。
 */
class Result<T>(
    val code: String? = null,
    val message: String? = null,
    val data: T? = null,
    val requestId: String? = null
) : Serializable {
    companion object {
        /**
         * 序列化版本ID，用于序列化和反序列化。
         */
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 5679018624309023727L

        /**
         * 表示操作成功的状态码。
         */
        const val SUCCESS_CODE = "0"
    }

    /**
     * 判断操作是否成功。
     *
     * @return 如果操作成功，则返回true；否则返回false。
     */
    fun isSuccess(): Boolean {
        return SUCCESS_CODE == code
    }
}


