package fausto.fan.project.framework.starter.distributedid.core

/**
 * IdGenerator是一个接口，用于定义生成唯一标识符的标准化方式。
 * 它提供了一种生成下一个唯一标识符和其字符串形式的标准化方法。
 */
interface IdGenerator {
    /**
     * 生成下一个唯一的长整型标识符。
     *
     * @return Long 类型的唯一标识符。默认值为0L
     */
    fun nextId(): Long = 0L

    /**
     * 生成下一个唯一的标识符字符串。
     *
     * @return String 类型的唯一标识符字符串。默认值为空字符串
     */
    fun nextIdStr(): String = ""
}
