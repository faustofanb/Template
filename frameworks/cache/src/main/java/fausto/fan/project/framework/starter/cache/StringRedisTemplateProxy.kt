@file:Suppress("UNCHECKED_CAST")

package fausto.fan.project.framework.starter.cache

import com.alibaba.fastjson2.JSON
import fausto.fan.project.framework.starter.cache.config.RedisDistributedProperties
import fausto.fan.project.framework.starter.cache.toolkit.CacheUtil
import fausto.fan.project.framework.starter.cache.toolkit.FastJson2Util
import org.redisson.api.RBloomFilter
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit

class StringRedisTemplateProxy(
    private val stringRedisTemplate: StringRedisTemplate,
    private val redisProperties: RedisDistributedProperties,
    private val redissonClient: RedissonClient
) : DistributedCache {

    companion object {
        const val LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua"
        const val SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:"
    }

    override fun <T> get(key: String, clazz: Class<T>, cacheLoader: () -> T, timeout: Long, timeUnit: TimeUnit): T? {
        val actualTimeUnit = if (timeUnit == TimeUnit.MILLISECONDS) {
            redisProperties.valueTimeUnit
        } else {
            timeUnit
        }

        val result = get(key, clazz)
        if (!CacheUtil.isNullOrBlank(result))
            return result
        return loadAndSet(key, cacheLoader, timeout, actualTimeUnit, true)

    }

    override fun <T> get(key: String, clazz: Class<T>): T? {
        val value = stringRedisTemplate.opsForValue().get(key)
        if (String::class.java.isAssignableFrom(clazz))
            return value as? T
        return JSON.parseObject(value, FastJson2Util.buildType(clazz))
    }

    override fun <T> safeGet(
        key: String,
        clazz: Class<T>,
        cacheLoader: () -> T,
        timeout: Long,
        timeUnit: TimeUnit,
        bloomFilter: RBloomFilter<String>?,
        cacheCheckFilter: ((param: String) -> Boolean)?,
        cacheGetIfAbsent: ((param: String) -> Void)?
    ): T? {
        var result = get(key, clazz)
        // 缓存结果不等于空或空字符串直接返回
        if (!CacheUtil.isNullOrBlank(result)
            || cacheCheckFilter?.invoke(key) == true
            || bloomFilter?.contains(key) == false
        ) {
            return result
        }

        val lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key)
        lock.lock()
        try {
            // 双重判定锁，减轻获得分布式锁后线程访问数据库压力
            result = get(key, clazz)
            if (CacheUtil.isNullOrBlank(result)) {
                result = loadAndSet(key, cacheLoader, timeout, timeUnit, true, bloomFilter)
                if (CacheUtil.isNullOrBlank(result)) {
                    cacheGetIfAbsent?.invoke(key)
                }
            }
        } finally {
            lock.unlock()
        }
        return result

    }

    override fun put(key: String, value: Any, timeout: Long, timeUnit: TimeUnit) {
        TODO("Not yet implemented")
    }

    override fun put(key: String, value: Any) {
        TODO("Not yet implemented")
    }

    override fun safePut(
        key: String,
        value: Any,
        timeout: Long,
        timeUnit: TimeUnit,
        bloomFilter: RBloomFilter<String>
    ) {
        TODO("Not yet implemented")
    }

    override fun countExistingKeys(vararg keys: String): Long {
        TODO("Not yet implemented")
    }

    override fun putIfAllAbsent(keys: Collection<String>): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(key: String?, keys: Collection<String>?): Long {
        TODO("Not yet implemented")
    }

    override fun hasKey(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getInstance(): Cache {
        TODO("Not yet implemented")
    }

    private fun <T> loadAndSet(
        key: String,
        cacheLoader: () -> T?,
        timeout: Long,
        timeUnit: TimeUnit,
        safeFlag: Boolean,
        bloomFilter: RBloomFilter<String>? = null
    ): T? {
        val result = cacheLoader()
        when {
            CacheUtil.isNullOrBlank(result) -> return result
            safeFlag -> safePut(key, result!!, timeout, timeUnit, bloomFilter!!)
            else -> put(key, result!!, timeout, timeUnit)
        }
        return result
    }
}