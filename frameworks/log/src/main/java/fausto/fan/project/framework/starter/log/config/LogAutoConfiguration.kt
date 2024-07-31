package fausto.fan.project.framework.starter.log.config

import fausto.fan.project.framework.starter.log.core.ILogPrintAspect
import org.springframework.context.annotation.Bean

class LogAutoConfiguration {

    @Bean
    fun iLogPrintAspect(): ILogPrintAspect {
        return ILogPrintAspect()
    }
}