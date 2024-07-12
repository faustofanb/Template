package fausto.fan.project.frameworks.starter.user.config

import fausto.fan.project.framework.starter.base.constant.USER_TRANSMIT_FILTER_ORDER
import fausto.fan.project.frameworks.starter.user.core.UserTransmitFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean

/**
 * 自动配置类，只在Web应用程序中生效。
 * 该类用于注册一个全局的用户传输过滤器，以实现用户信息的传递和处理。
 */
@ConditionalOnWebApplication
class UserAutoConfiguration {
    /**
     * 注册一个用户传输过滤器，用于在每个请求中传递用户信息。
     *
     * @return FilterRegistrationBean<UserTransmitFilter> 对象，用于配置和注册过滤器。
     */
    @Bean
    fun globalUserTransmitFilter(): FilterRegistrationBean<UserTransmitFilter> {
        return FilterRegistrationBean<UserTransmitFilter>().apply {
            filter = UserTransmitFilter()
            urlPatterns = listOf("/*")
            order = USER_TRANSMIT_FILTER_ORDER
        }
    }
}
