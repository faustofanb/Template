package fausto.fan.project.framework.starter.database.toolkit

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import fausto.fan.project.framework.starter.common.toolkit.BeanUtil
import fausto.fan.project.framework.starter.convention.page.PageRequest
import fausto.fan.project.framework.starter.convention.page.PageResponse

/**
 * 分页数据转换工具类。
 */
object PageUtil {

    /**
     * 将 PageRequest 转换为泛型的 Page 对象。
     *
     * @param pageRequest 包含当前页和每页大小的分页请求参数
     * @return 返回一个泛型类型为 Any 的 Page 对象
     */
    fun convert(pageRequest: PageRequest): Page<Any> = convert(pageRequest.current, pageRequest.size)

    /**
     * 根据指定的当前页和每页大小创建 Page 对象。
     *
     * @param current 当前页码
     * @param size 每页项的数量
     * @return 返回一个泛型类型为 Any 的 Page 对象
     */
    fun convert(current: Long, size: Long): Page<Any> = Page<Any>(current, size)


    /**
     * 将 IPage 转换为泛型的 PageResponse 对象。
     *
     * @param iPage 包含分页数据的输入 IPage 对象
     * @return 返回一个泛型类型为 Any 的 PageResponse 对象
     */
    fun convert(iPage: IPage<Any>): PageResponse<Any> = buildConventionPage(iPage)


    /**
     * 使用 BeanUtil 进行数据转换，将 IPage 转换为具有指定泛型类型的 PageResponse 对象。
     *
     * @param iPage 包含原始数据的输入 IPage 对象
     * @param targetClass 转换的目标类类型
     * @return 返回具有指定泛型类型的 PageResponse 对象
     */
    @Suppress("UNCHECKED_CAST")
    fun <TARGET, ORIGINAL> convert(iPage: IPage<ORIGINAL>, targetClass: Class<TARGET>): PageResponse<TARGET> {
        return buildConventionPage(
            iPage.convert {
                BeanUtil.convert(it, targetClass)
            }
        ) as PageResponse<TARGET>
    }


    /**
     * 使用自定义映射函数进行数据转换，将 IPage 转换为具有指定泛型类型的 PageResponse 对象。
     *
     * @param iPage 包含原始数据的输入 IPage 对象
     * @param mapper 定义如何将每一项原始数据转换为目标数据的函数
     * @return 返回具有指定泛型类型的 PageResponse 对象
     */
    fun <TARGET, ORIGINAL> convert(
        iPage: IPage<ORIGINAL>,
        mapper: (source: ORIGINAL) -> TARGET
    ): PageResponse<TARGET> {
        val targetDataList = iPage.records.map(mapper).toList()
        return PageResponse<TARGET>().apply {
            this.current = iPage.current
            this.size = iPage.size
            this.total = iPage.total
            this.records = targetDataList
        }
    }


    /**
     * 构建常规的 PageResponse 对象的私有方法。
     *
     * @param iPage 包含分页数据的输入 IPage 对象
     * @return 返回一个泛型类型为 Any 的 PageResponse 对象
     */
    private fun buildConventionPage(iPage: IPage<Any>): PageResponse<Any> {
        return PageResponse<Any>().apply {
            this.current = iPage.current
            this.size = iPage.size
            this.total = iPage.total
            this.records = iPage.records
        }
    }
}
