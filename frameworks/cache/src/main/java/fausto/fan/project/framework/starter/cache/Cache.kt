package fausto.fan.project.framework.starter.cache

import org.redisson.api.RBloomFilter
import java.util.concurrent.TimeUnit

/**
 * 缓存接口，定义了缓存操作的基本方法。
 */
interface Cache {
    /**
     * 从缓存中获取指定键的值。
     *
     * @param key 键的字符串表示。
     * @param clazz 值的类类型，用于反序列化。
     * @param <T> 值的泛型类型。
     * @return 缓存中对应键的值，如果不存在则返回null。
     */
    fun <T> get(key: String, clazz: Class<T>): T?

    /**
     * 将键值对放入缓存。
     *
     * @param key 键的字符串表示。
     * @param value 要缓存的值。
     */
    fun put(key: String, value: Any)

    /**
     * 如果所有给定的键都不存在于缓存中，则将它们全部放入缓存。
     *
     * @param keys 要放入缓存的键的集合。
     * @return 如果所有键都不存在且成功放入缓存，则返回true；否则返回false。
     */
    fun putIfAllAbsent(keys: Collection<String>): Boolean

    /**
     * 删除缓存中的键值对。
     *
     * @param key 要删除的键，可以为null，此时keys不能为null。
     * @param keys 要删除的键的集合，可以为null，此时key不能为null。
     * @return 被删除的键的数量。
     */
    fun delete(key: String? = null, keys: Collection<String>? = null): Long

    /**
     * 检查缓存中是否存在指定的键。
     *
     * @param key 要检查的键。
     * @return 如果缓存中存在指定的键，则返回true；否则返回false。
     */
    fun hasKey(key: String): Boolean

    /**
     * 获取缓存实例。
     *
     * @return 缓存的实例。
     */
    fun getInstance(): Cache
}

/**
 * 分布式缓存接口，继承自Cache接口，提供了在分布式环境下使用缓存的能力。
 */
interface DistributedCache : Cache {

    /**
     * 从缓存中获取指定键的值，如果缓存中不存在，则使用提供的加载函数加载数据并放入缓存。
     *
     * @param key 缓存的键。
     * @param clazz 值的类类型。
     * @param cacheLoader 加载数据的函数。
     * @param timeout 超时时间。
     * @param timeUnit 时间单位。
     * @return 缓存的值，如果不存在则为null。
     */
    fun <T> get(
        key: String,
        clazz: Class<T>,
        cacheLoader: () -> T,
        timeout: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS
    ): T?

    /**
     * 安全地从缓存中获取值，提供了过滤和缓存不存在时的处理逻辑。
     *
     * @param key 缓存的键。
     * @param clazz 值的类类型。
     * @param cacheLoader 加载数据的函数。
     * @param timeout 超时时间。
     * @param timeUnit 时间单位。
     * @param bloomFilter 布隆过滤器，用于过滤不存在的键。
     * @param cacheCheckFilter 数据检查过滤器，用于进一步过滤缓存中的数据。
     * @param cacheGetIfAbsent 缓存不存在时的处理函数。
     * @return 缓存的值，如果不存在则为null。
     */
    fun <T> safeGet(
        key: String,
        clazz: Class<T>,
        cacheLoader: () -> T,
        timeout: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        bloomFilter: RBloomFilter<String>? = null,
        cacheCheckFilter: ((param: String) -> Boolean)? = null,
        cacheGetIfAbsent: ((param: String) -> Void)? = null
    ): T?

    /**
     * 将键值对放入缓存，并设置超时时间。
     *
     * @param key 缓存的键。
     * @param value 缓存的值。
     * @param timeout 超时时间。
     * @param timeUnit 时间单位。
     */
    fun put(key: String, value: Any, timeout: Long, timeUnit: TimeUnit)

    /**
     * 安全地将键值对放入缓存，提供了布隆过滤器来避免重复数据。
     *
     * @param key 缓存的键。
     * @param value 缓存的值。
     * @param timeout 超时时间。
     * @param timeUnit 时间单位。
     * @param bloomFilter 布隆过滤器，用于检查是否已存在相同键。
     */
    fun safePut(
        key: String,
        value: Any,
        timeout: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        bloomFilter: RBloomFilter<String>
    )

    /**
     * 统计现有键的数量。
     *
     * @param keys 多个键。
     * @return 存在的键的数量。
     */
    fun countExistingKeys(vararg keys: String): Long
}


/**
 * MultistageCache接口定义了一个多阶段缓存，它是Cache接口的子接口。
 * 多阶段缓存允许在不同的缓存阶段存储和检索数据，以优化访问效率和缓存策略。
 */
interface MultistageCache : Cache




















