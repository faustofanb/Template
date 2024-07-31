package fausto.fan.project.framework.starter.log.core

/**
 * 数据类ILogPrintDTO，用于日志打印功能。
 * 该类用于封装日志相关的参数，包括开始时间、输入参数和输出参数。
 *
 * @property beginTime 开始时间，表示日志记录开始的时间点。
 * @property inputParams 输入参数数组，包含日志记录开始时的输入数据。
 * @property outputParams 输出参数，可以是任意类型的对象，表示日志记录结束时的输出数据。
 */
data class ILogPrintDTO(
    var beginTime: String? = null,
    var inputParams: Array<Any>? = null,
    var outputParams: Any? = null,
) {
    /**
     * 重写equals方法，用于比较两个ILogPrintDTO对象是否相等。
     *
     * @param other 待比较的对象。
     * @return 如果两个对象的beginTime、inputParams和outputParams属性都相同，则返回true，否则返回false。
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ILogPrintDTO

        if (beginTime != other.beginTime) return false
        if (!inputParams.contentEquals(other.inputParams)) return false
        if (outputParams != other.outputParams) return false

        return true
    }

    /**
     * 重写hashCode方法，用于生成ILogPrintDTO对象的哈希码。
     * 这个方法用于支持需要哈希码的功能，比如在HashMap中作为键使用。
     *
     * @return 返回该对象的哈希码。
     */
    override fun hashCode(): Int {
        var result = beginTime?.hashCode() ?: 0
        result = 31 * result + inputParams.contentHashCode()
        result = 31 * result + (outputParams?.hashCode() ?: 0)
        return result
    }
}

