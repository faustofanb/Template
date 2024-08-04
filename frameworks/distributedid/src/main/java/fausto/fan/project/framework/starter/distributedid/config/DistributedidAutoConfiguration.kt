package fausto.fan.project.framework.starter.distributedid.config

import fausto.fan.project.framework.starter.base.ApplicationContextHolder
import fausto.fan.project.framework.starter.distributedid.core.snowflake.LocalRedisWorkIdChoose
import fausto.fan.project.framework.starter.distributedid.core.snowflake.RandomWorkIdChoose
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

@Import(ApplicationContextHolder::class)
class DistributedidAutoConfiguration {

    @Bean
    @ConditionalOnProperty("spring.data.redis.host")
    fun redisWorkIdChoose(): LocalRedisWorkIdChoose {
        return LocalRedisWorkIdChoose()
    }

    @Bean
    @ConditionalOnMissingBean(LocalRedisWorkIdChoose::class)
    fun randomWorkIdChoose(): RandomWorkIdChoose {
        return RandomWorkIdChoose()
    }
}