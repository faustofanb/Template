package fausto.fan.project.framework.starter.cache.config

import fausto.fan.project.framework.starter.cache.RedisKeySerializer
import org.redisson.api.RBloomFilter
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@EnableConfigurationProperties(BloomFilterPenetrateProperties::class, RedisDistributedProperties::class)
class CacheAutoConfiguration(
    private val redisDistributedProperties: RedisDistributedProperties
) {
    @Bean
    fun redisKeySerializer(): RedisKeySerializer {
        return RedisKeySerializer(redisDistributedProperties.prefix, redisDistributedProperties.prefixCharset)
    }

    @Bean
    @ConditionalOnProperty(
        prefix = BloomFilterPenetrateProperties.PREFIX,
        name = ["enabled"],
        havingValue = "true",
    )
    fun cachePenetrationBloomFilter(
        redissonClient: RedissonClient,
        bloomFilterPenetrateProperties: BloomFilterPenetrateProperties
    ): RBloomFilter<String> {
        return redissonClient.getBloomFilter<String>(bloomFilterPenetrateProperties.name)
            .apply {
                this.tryInit(
                    bloomFilterPenetrateProperties.expectedInsertions,
                    bloomFilterPenetrateProperties.falseProbability
                )
            }
    }

    //todo
}