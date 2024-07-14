package fausto.fan.project.framework.starter.designpattern.strategy

import fausto.fan.project.framework.starter.base.ApplicationContextHolder
import fausto.fan.project.framework.starter.base.init.ApplicationInitializingEvent
import fausto.fan.project.framework.starter.convention.exception.ServiceException
import org.springframework.context.ApplicationListener
import org.springframework.util.StringUtils
import java.util.regex.Pattern

/**
 * 抽象执行策略接口，用于定义特定请求的处理策略。
 *
 * 该接口泛型化，适用于不同类型的请求和响应对象。
 * 提供了标记方法、模式匹配标记方法、执行请求的方法以及根据请求返回响应的方法。
 */
interface AbstractExecuteStrategy<REQUEST, RESPONSE> {
    /**
     * 标记方法，用于标识当前策略的特定标记。
     * 返回一个字符串标记，如果没有特定标记，则返回null。
     */
    fun mark(): String? = null

    /**
     * 模式匹配标记方法，用于标识当前策略是否匹配特定的模式。
     * 返回一个字符串标记，如果没有特定模式匹配标记，则返回null。
     */
    fun patternMatchMark(): String? = null

    /**
     * 执行请求的方法。
     * 接收一个类型为REQUEST的参数，该参数代表需要处理的请求。
     * 该方法是一个空实现，子类需要根据具体需求重写该方法以实现请求的处理。
     */
    fun execute(requestParam: REQUEST) {}

    /**
     * 根据请求执行并返回响应的方法。
     * 接收一个类型为REQUEST的参数，该参数代表需要处理的请求。
     * 返回一个类型为RESPONSE的对象，代表处理请求后的响应结果。
     * 如果无法生成响应结果，则返回null。
     */
    fun executeResp(requestParam: REQUEST): RESPONSE? = null
}

/**
 * 抽象策略选择类，用于根据标记选择对应的执行策略。
 *
 */
class AbstractStrategyChoose : ApplicationListener<ApplicationInitializingEvent> {

    /**
     * 存储执行策略的映射，键为策略标记，值为执行策略实例。
     */
    private val abstractExecuteStrategyMap = HashMap<String, AbstractExecuteStrategy<*, *>>()

    /**
     * 根据标记和条件选择执行策略。
     *
     * @param mark 用于选择执行策略的标记。
     * @param predicateFlag 条件标志，用于决定是根据正则表达式匹配选择策略还是直接根据标记选择。
     * @return 返回选择的执行策略。
     * @throws ServiceException 如果没有找到对应的执行策略，则抛出此异常。
     */
    @Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")
    fun <REQUEST, RESPONSE> choose(mark: String, predicateFlag: Boolean): AbstractExecuteStrategy<REQUEST, RESPONSE> {
        if (predicateFlag) {
            val abstractExecuteStrategy = abstractExecuteStrategyMap.values
                .filter { StringUtils.hasText(it.patternMatchMark()) }
                .first { Pattern.compile(it.patternMatchMark()!!).matcher(mark).matches() }
                    as? AbstractExecuteStrategy<REQUEST, RESPONSE>
            return abstractExecuteStrategy ?: throw ServiceException("策略未定义")
        }
        return abstractExecuteStrategyMap[mark] as? AbstractExecuteStrategy<REQUEST, RESPONSE>
            ?: throw ServiceException("[$mark] 策略未定义")
    }

    /**
     * 根据标记选择执行策略并执行策略的请求方法。
     *
     * @param mark 用于选择执行策略的标记。
     * @param requestParam 执行策略的请求参数。
     * @param predicateFlag 条件标志，用于决定是根据正则表达式匹配选择策略还是直接根据标记选择。
     */
    fun <REQUEST, RESPONSE> chooseAndExecute(mark: String, requestParam: REQUEST, predicateFlag: Boolean = false) {
        val strategy: AbstractExecuteStrategy<REQUEST, *> = choose<REQUEST, RESPONSE>(mark, predicateFlag)
        strategy.execute(requestParam)
    }

    /**
     * 根据标记选择执行策略并执行策略的请求方法，返回响应结果。
     *
     * @param mark 用于选择执行策略的标记。
     * @param requestParam 执行策略的请求参数。
     * @return 执行策略后的响应结果。
     * @throws ServiceException 如果没有找到对应的执行策略，则抛出此异常。
     */
    fun <REQUEST, RESPONSE> chooseAndExecuteResp(mark: String, requestParam: REQUEST): RESPONSE? {
        return choose<REQUEST, RESPONSE>(mark, false).executeResp(requestParam)
    }

    /**
     * 应用初始化事件监听方法，用于在应用初始化时注册所有的执行策略。
     *
     * @param event 应用初始化事件。
     */
    override fun onApplicationEvent(event: ApplicationInitializingEvent) {
        ApplicationContextHolder.getBeansOfType(AbstractExecuteStrategy::class.java).forEach { (_, bean) ->
            val beanExist = abstractExecuteStrategyMap[bean.mark()]
            if (beanExist != null) throw ServiceException("[${bean.mark()}] Duplicate execution policy")
            abstractExecuteStrategyMap[bean.mark()!!] = bean
        }
    }
}
