package fausto.fan.project.framework.starter.web.config

import fausto.fan.project.framework.starter.web.GlobalExceptionHandler
import fausto.fan.project.framework.starter.web.initialize.InitializeDispatcherServletController
import fausto.fan.project.framework.starter.web.initialize.InitializeDispatcherServletHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

/**
 * Web自动配置类，用于配置Web应用中的全局异常处理器、DispatcherServlet控制器及初始化工作
 */
class WebAutoConfiguration {

    /**
     * 当全局异常处理器不存在时创建一个
     *
     * @return GlobalExceptionHandler 全局异常处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun globalExceptionHandler(): GlobalExceptionHandler {
        return GlobalExceptionHandler()
    }

    /**
     * 初始化DispatcherServlet控制器
     *
     * @return InitializeDispatcherServletController 初始化后的DispatcherServlet控制器实例
     */
    @Bean
    fun initializeDispatcherServletController(): InitializeDispatcherServletController {
        return InitializeDispatcherServletController()
    }

    /**
     * 创建一个简单的RestTemplate实例
     *
     * @param factory ClientHttpRequestFactory实例，用于创建HTTP请求
     * @return RestTemplate 使用指定的ClientHttpRequestFactory实例化后的RestTemplate
     */
    @Bean
    fun simpleRestTemplate(factory: ClientHttpRequestFactory): RestTemplate {
        return RestTemplate(factory)
    }

    /**
     * 创建一个简单的ClientHttpRequestFactory实例，并设置读取和连接超时时间
     *
     * @return ClientHttpRequestFactory 初始化后的ClientHttpRequestFactory实例
     */
    @Bean
    fun simpleClientHttpRequestFactory(): ClientHttpRequestFactory {
        return SimpleClientHttpRequestFactory().apply {
            setReadTimeout(5000)
            setConnectTimeout(5000)
        }
    }

    /**
     * 初始化DispatcherServlet处理器映射
     *
     * @param simpleRestTemplate RestTemplate实例，用于处理REST请求
     * @param configurableEnvironment 可配置的环境，用于获取配置信息
     * @return InitializeDispatcherServletHandler 初始化后的DispatcherServlet处理器映射实例
     */
    @Bean
    fun initializeDispatcherServletHandler(
        simpleRestTemplate: RestTemplate,
        configurableEnvironment: ConfigurableEnvironment
    ): InitializeDispatcherServletHandler {
        return InitializeDispatcherServletHandler(simpleRestTemplate, configurableEnvironment)
    }
}
