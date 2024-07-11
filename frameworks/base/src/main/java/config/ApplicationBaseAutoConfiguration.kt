package config

import ApplicationContextHolder
import init.ApplicationContentPostPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import safa.FastJsonSafeMode

/**
 * 应用程序基础配置类，用于在Spring Boot应用程序中自动配置一些常见的Bean。
 * 这个类的作用是检查Spring应用程序上下文中是否缺少某些Bean，如果缺少，则自动创建这些Bean。
 */
class ApplicationBaseAutoConfiguration {

    /**
     * 提供ApplicationContextHolder的Bean定义。
     * 如果ApplicationContextHolder bean不存在，则创建一个。
     * 这允许在应用程序中轻松访问应用程序上下文。
     *
     * @return ApplicationContextHolder 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun applicationContextHolder(): ApplicationContextHolder {
        return ApplicationContextHolder
    }

    /**
     * 提供ApplicationContentPostPostProcessor的Bean定义。
     * 如果ApplicationContentPostPostProcessor bean不存在，则创建一个。
     * 这个处理器用于在应用程序内容加载后进行一些自定义处理。
     *
     * @param applicationContext Spring应用程序上下文
     * @return ApplicationContentPostPostProcessor 实例
     */
    @Bean
    @ConditionalOnMissingBean
    fun applicationContentPostProcessor(applicationContext: ApplicationContext): ApplicationContentPostPostProcessor {
        return ApplicationContentPostPostProcessor(applicationContext)
    }

    /**
     * 提供FastJsonSafeMode的Bean定义。
     * 如果FastJsonSafeMode bean不存在，并且配置属性"framework.fastjson.safa-mode"为true，则创建一个。
     * 这个模式用于启用FastJson的安全模式，以防止一些潜在的安全问题。
     *
     * @return FastJsonSafeMode 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = ["framework.fastjson.safa-mode"], havingValue = "true")
    fun fastJsonSafeMode(): FastJsonSafeMode {
        return FastJsonSafeMode()
    }
}
