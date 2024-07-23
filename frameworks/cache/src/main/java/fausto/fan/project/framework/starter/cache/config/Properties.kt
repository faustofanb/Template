package fausto.fan.project.framework.starter.cache.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.concurrent.TimeUnit

/**
 * 此数据类用于配置布隆过滤器(Bloom Filter)防止缓存穿透的属性。
 * 属性包括布隆过滤器的名称、预期插入次数以及假阳性概率。
 *
 * @param name 布隆过滤器的名称，默认为"cache_penetration_bloom_filter"。
 * @param expectedInsertions 预期插入到布隆过滤器中的条目数量，默认为64。
 * @param falseProbability 允许的假阳性概率，默认为0.03。
 */
@ConfigurationProperties(prefix = BloomFilterPenetrateProperties.PREFIX)
data class BloomFilterPenetrateProperties(
    val name: String = "cache_penetration_bloom_filter",
    val expectedInsertions: Long = 64L,
    val falseProbability: Double = 0.03
) {
    companion object {
        const val PREFIX: String = "framework.cache.redis.bloom-filter.default"
    }
}

/**
 * 此数据类用于配置Redis分布式缓存的属性。
 * 属性包括缓存键前缀、前缀字符集、值超时时间以及时间单位。
 *
 * @param prefix 缓存键的前缀，默认为空字符串。
 * @param prefixCharset 缓存键前缀的字符集，默认为"UTF-8"。
 * @param valueTimeout 缓存值的超时时间，默认为30000毫秒。
 * @param valueTimeUnit 超时时间的时间单位，默认为毫秒。
 */
@ConfigurationProperties(prefix = RedisDistributedProperties.PREFIX)
data class RedisDistributedProperties(
    val prefix: String = "",
    val prefixCharset: String = "UTF-8",
    val valueTimeout: Long = 30000L,
    val valueTimeUnit: TimeUnit = TimeUnit.MILLISECONDS
) {
    companion object {
        const val PREFIX: String = "framework.cache.redis"
    }
}
