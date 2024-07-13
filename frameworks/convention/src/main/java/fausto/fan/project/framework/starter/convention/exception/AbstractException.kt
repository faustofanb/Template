package fausto.fan.project.framework.starter.convention.exception

import fausto.fan.project.framework.starter.convention.errorcode.BaseErrorCode
import fausto.fan.project.framework.starter.convention.errorcode.IErrorCode

/**
 * 抽象异常类，用于封装带有错误代码和错误消息的异常。
 * 该类继承自RuntimeException，添加了错误代码和错误消息的处理。
 */
abstract class AbstractException(
    message: String?,
    throwable: Throwable?,
    errorCode: IErrorCode
) : RuntimeException(message, throwable) {
    /** 错误代码，由errorCode.code()获取。 */
    val errorCode: String = errorCode.code()

    /** 错误消息，优先使用传入的消息，否则使用错误代码的默认消息。 */
    val errorMessage: String = message ?: errorCode.message()
}

/**
 * 客户端异常类，用于表示客户端侧的异常情况。
 * 该类继承自AbstractException，提供了多种构造方法以方便使用。
 */
class ClientException : AbstractException {
    /**
     * 带有完整信息的构造方法。
     * @param message 异常的详细消息。
     * @param throwable 异常链，可为空。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, throwable: Throwable?, errorCode: IErrorCode) : super(message, throwable, errorCode)

    /**
     * 带有消息和错误代码的构造方法。
     * @param message 异常的详细消息。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, errorCode: IErrorCode) : this(message, null, errorCode)

    /**
     * 带有消息的构造方法，使用默认的错误代码。
     * @param message 异常的详细消息。
     */
    constructor(message: String?) : this(message, null, BaseErrorCode.CLIENT_ERROR)

    /**
     * 带有错误代码的构造方法，使用默认的消息。
     * @param errorCode 错误代码接口实例。
     */
    constructor(errorCode: IErrorCode) : this(null, null, errorCode)

    /**
     * 返回异常的字符串表示，包含错误代码和错误消息。
     * @return 异常的字符串描述。
     */
    override fun toString(): String {
        return "ClientException{" +
                "code = '${errorCode}'," +
                "message = '${errorMessage}'" +
                "}"
    }
}

/**
 * 服务端异常类，用于表示服务端侧的异常情况。
 * 该类继承自AbstractException，提供了多种构造方法以方便使用。
 */
class ServiceException : AbstractException {
    /**
     * 带有完整信息的构造方法。
     * @param message 异常的详细消息。
     * @param throwable 异常链，可为空。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, throwable: Throwable?, errorCode: IErrorCode) : super(message, throwable, errorCode)

    /**
     * 带有消息和错误代码的构造方法。
     * @param message 异常的详细消息。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, errorCode: IErrorCode) : this(message, null, errorCode)

    /**
     * 带有消息的构造方法，使用默认的错误代码。
     * @param message 异常的详细消息。
     */
    constructor(message: String?) : this(message, null, BaseErrorCode.SERVICE_ERROR)

    /**
     * 带有错误代码的构造方法，使用默认的消息。
     * @param errorCode 错误代码接口实例。
     */
    constructor(errorCode: IErrorCode) : this(null, null, errorCode)

    /**
     * 返回异常的字符串表示，包含错误代码和错误消息。
     * @return 异常的字符串描述。
     */
    override fun toString(): String {
        return "ServiceException{" +
                "code = '${errorCode}'," +
                "message = '${errorMessage}'" +
                "}"
    }
}

/**
 * 远程异常类，用于表示远程调用过程中的异常情况。
 * 该类继承自AbstractException，提供了多种构造方法以方便使用。
 */
class RemoteException : AbstractException {
    /**
     * 带有完整信息的构造方法。
     * @param message 异常的详细消息。
     * @param throwable 异常链，可为空。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, throwable: Throwable?, errorCode: IErrorCode) : super(message, throwable, errorCode)

    /**
     * 带有消息和错误代码的构造方法。
     * @param message 异常的详细消息。
     * @param errorCode 错误代码接口实例。
     */
    constructor(message: String?, errorCode: IErrorCode) : this(message, null, errorCode)

    /**
     * 带有消息的构造方法，使用默认的错误代码。
     * @param message 异常的详细消息。
     */
    constructor(message: String?) : this(message, null, BaseErrorCode.REMOTE_ERROR)

    /**
     * 返回异常的字符串表示，包含错误代码和错误消息。
     * @return 异常的字符串描述。
     */
    override fun toString(): String {
        return "RemoteException{" +
                "code = '${errorCode}'," +
                "message = '${errorMessage}'" +
                "}"
    }
}

