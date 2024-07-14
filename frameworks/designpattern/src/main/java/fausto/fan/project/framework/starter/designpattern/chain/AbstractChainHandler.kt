package fausto.fan.project.framework.starter.designpattern.chain

import fausto.fan.project.framework.starter.base.ApplicationContextHolder
import org.springframework.boot.CommandLineRunner
import org.springframework.core.Ordered

/**
 * 抽象链处理程序接口。
 *
 * 该接口定义了一个处理链中的元素应具有的行为。处理链中的每个元素都实现了特定的处理逻辑，并且可以被有序地组织在一起。
 * 它们可以按照一定的顺序处理请求参数，并通过实现[mark]方法来标识自己。
 *
 * @param T 处理器处理的请求参数的类型。
 */
interface AbstractChainHandler<T> : Ordered {
    /**
     * 处理给定的请求参数。
     *
     * 该方法实现了处理链中的元素对特定请求参数的处理逻辑。每个处理程序如何处理参数取决于其具体实现。
     *
     * @param requestParam 需要被处理的请求参数。
     */
    fun handler(requestParam: T)

    /**
     * 返回当前处理程序的标识。
     *
     * 该方法用于标识处理链中的每个处理程序。返回的字符串可以是任何可以唯一标识处理程序的值。
     * 这个标识符可以用于跟踪处理链的状态，或者在需要时对处理链进行调试。
     *
     * @return 当前处理程序的标识字符串。
     */
    fun mark(): String
}

/**
 * 抽象链上下文类，用于管理特定类型的链处理程序。
 *
 * @param T 链处理程序的请求参数类型。
 */
class AbstractChainContext<T> : CommandLineRunner {

    /**
     * 存储特定标识符的链处理程序容器。
     */
    private val abstractChainHandlerContainer = HashMap<String, MutableList<AbstractChainHandler<T>>>()

    /**
     * 根据标识符触发链处理程序处理请求。
     *
     * @param mark 链处理程序的标识符。
     * @param requestParam 链处理程序的请求参数。
     * @throws RuntimeException 如果标识符未定义，则抛出异常。
     */
    fun handler(mark: String, requestParam: T) {
        val abstractChainHandlers = abstractChainHandlerContainer[mark]
            ?: throw RuntimeException("[$mark] Chain of Responsibility ID is undefined.")
        abstractChainHandlers.forEach {
            it.handler(requestParam)
        }
    }

    /**
     * 初始化链处理程序。从应用上下文中获取所有链处理程序实例，并根据标识符注册它们。
     *
     * @param args 命令行参数，本方法不使用。
     * @throws Exception 如果初始化过程中发生错误，则抛出异常。
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(Exception::class)
    override fun run(vararg args: String?) {
        val chainFilterMap: Map<String, AbstractChainHandler<T>> =
            ApplicationContextHolder.getBeansOfType(AbstractChainHandler::class.java)
                    as Map<String, AbstractChainHandler<T>>
        chainFilterMap.forEach { (_, bean) ->
            val abstractChainHandlers = abstractChainHandlerContainer[bean.mark()] ?: ArrayList()
            abstractChainHandlers.add(bean)

            // 根据处理程序的顺序对链进行排序
            abstractChainHandlerContainer[bean.mark()] = abstractChainHandlers.sortedBy { it.order }.toMutableList()
        }
    }
}
