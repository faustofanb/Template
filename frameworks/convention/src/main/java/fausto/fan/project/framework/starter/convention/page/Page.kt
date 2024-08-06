package fausto.fan.project.framework.starter.convention.page

import java.io.Serializable
import java.util.function.Function

/**
 * 分页请求类，用于指定分页参数。
 *
 * @param current 当前页码，默认为1。
 * @param size 每页的记录数，默认为10。
 */
data class PageRequest(var current: Long = 1L, var size: Long = 10L)

/**
 * 分页响应类，用于承载分页后的数据及分页信息。
 *
 * @param current 当前页码。
 * @param size 每页的记录数。
 * @param total 总记录数。
 * @param records 分页后的数据列表。
 */
data class PageResponse<T>(
    var current: Long = 1L,
    var size: Long = 10L,
    var total: Long = 0L,
    var records: List<T> = emptyList()
) : Serializable {
    companion object {
        /**
         * 序列化版本号，用于Java序列化机制。
         */
        @Suppress("ConstPropertyName")
        private const val serialVersionUID: Long = 1L
    }

    /**
     * 将当前分页响应中的数据转换为另一种类型。
     *
     * @param mapper 数据转换的函数。
     * @return 转换后的分页响应。
     */
    fun <R> convert(mapper: Function<in T, out R>): PageResponse<R> {
        var collect: List<R> = this.records.stream().map(mapper).toList()
        return PageResponse(records = collect)
    }
}
