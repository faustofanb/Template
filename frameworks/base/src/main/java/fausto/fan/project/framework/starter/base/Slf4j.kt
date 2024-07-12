package fausto.fan.project.framework.starter.base

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Slf4j {
    companion object {
        val <reified T> T.log: Logger
            inline get() = LoggerFactory.getLogger(T::class.java)
    }
}