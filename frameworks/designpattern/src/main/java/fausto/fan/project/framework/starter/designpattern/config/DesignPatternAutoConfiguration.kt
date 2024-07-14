package fausto.fan.project.framework.starter.designpattern.config

import fausto.fan.project.framework.starter.base.config.ApplicationBaseAutoConfiguration
import fausto.fan.project.framework.starter.designpattern.chain.AbstractChainContext
import fausto.fan.project.framework.starter.designpattern.strategy.AbstractStrategyChoose
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Bean

/**
 * 自动配置类，用于根据条件加载特定的设计模式实现。
 * 该类通过@ImportAutoConfiguration注解指定了一个应用基础自动配置类，该类将根据条件决定是否启用。
 */
@ImportAutoConfiguration(ApplicationBaseAutoConfiguration::class)
class DesignPatternAutoConfiguration {

    /**
     * 定义一个Bean，用于创建AbstractStrategyChoose的实例。
     * AbstractStrategyChoose是一个策略模式的抽象类，提供了一个选择策略的接口。
     * 返回值：返回一个AbstractStrategyChoose的实例，该实例可以被其他Bean引用和使用。
     */
    @Bean
    fun abstractStrategyChoose(): AbstractStrategyChoose {
        return AbstractStrategyChoose()
    }

    /**
     * 使用泛型定义一个Bean，用于创建AbstractChainContext的实例。
     * AbstractChainContext是一个责任链模式的抽象类，提供了一个处理请求的链式结构。
     * 参数<T>：表示AbstractChainContext可以处理不同类型的数据。
     * 返回值：返回一个泛型的AbstractChainContext实例，该实例可以被其他Bean引用和使用。
     */
    @Bean
    fun <T> abstractChainContext(): AbstractChainContext<T> {
        return AbstractChainContext()
    }
}
