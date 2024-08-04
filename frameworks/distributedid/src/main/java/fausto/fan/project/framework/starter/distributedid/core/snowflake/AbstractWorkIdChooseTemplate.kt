package fausto.fan.project.framework.starter.distributedid.core.snowflake

import cn.hutool.core.collection.CollUtil
import fausto.fan.project.framework.starter.base.ApplicationContextHolder
import fausto.fan.project.framework.starter.base.Slf4j
import fausto.fan.project.framework.starter.base.Slf4j.Companion.log
import fausto.fan.project.framework.starter.distributedid.toolkit.SnowflakeIdUtil
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.scripting.support.ResourceScriptSource

/**
 * AbstractWorkIdChooseTemplate 是一个抽象类，用于提供一个模板方法来选择和初始化 Snowflake 算法的工作ID。
 * 它支持通过配置决定是否使用系统时钟。
 */
@Slf4j
abstract class AbstractWorkIdChooseTemplate {
    // 标记是否使用系统时钟作为时间戳部分的标志，通过配置文件决定，默认为 false。
    @Value("\${framework.distributed.id.snowflake.is-use-system-clock}")
    private val isUseSystemClock: Boolean = false

    /**
     * 选择工作ID的抽象方法。
     * 该方法应由子类实现，用于确定 Snowflake 算法所需的工作ID和数据中心ID。
     *
     * @return 返回一个 WorkIdWrapper 对象，包含工作ID和数据中心ID。
     */
    abstract fun chooseWorkId(): WorkIdWrapper

    /**
     * 选择并初始化 Snowflake 算法的工作ID。
     * 该方法首先调用 chooseWorkId 方法获取工作ID和数据中心ID，然后使用这些值初始化 Snowflake 实例，
     * 并记录初始化信息。
     */
    fun chooseAndInit() {
        // 解构调用 chooseWorkId 方法的返回值，获取工作ID和数据中心ID。
        val (workId, dataCenterId) = chooseWorkId()
        // 使用获取到的工作ID、数据中心ID和是否使用系统时钟的配置来创建 Snowflake 实例。
        val snowflake = Snowflake(workId!!, dataCenterId!!, isUseSystemClock)
        // 记录 Snowflake 实例的初始化信息。
        log.info(
            """
            Snowflake { type: ${this::class.java.simpleName}, workId: $workId, dataCenterId: $dataCenterId}
        """.trimIndent()
        )
        // 初始化 SnowflakeIdUtil 的 Snowflake 实例。
        SnowflakeIdUtil.initSnowflake(snowflake)
    }
}

/**
 * 本地Redis工作ID选择器
 * 该类通过RedisLua脚本从Redis中选择工作ID
 */
@Slf4j
class LocalRedisWorkIdChoose(
    // 默认使用ApplicationContextHolder获取StringRedisTemplate实例
    private val stringRedisTemplate: StringRedisTemplate = ApplicationContextHolder.getBean(clazz = StringRedisTemplate::class.java)!!
): AbstractWorkIdChooseTemplate(), InitializingBean{
    /**
     * 选择工作ID
     * @return 返回工作ID封装
     */
    override fun chooseWorkId(): WorkIdWrapper {
        // 设置Redis脚本
        val redisScript = DefaultRedisScript<List<Long>>().apply {
            setScriptSource(ResourceScriptSource(ClassPathResource("lua/chooseWorkIdLua.lua")))
        }
        var luaResultList: List<Long>? = null

        // 执行Redis脚本获取工作ID
        try {
            luaResultList = stringRedisTemplate.execute(redisScript, mutableListOf())
        } catch (ex: Exception) {
            // 异常处理：记录错误日志
            log.error("Redis Lua 脚本获取 WorkId 失败", ex)
        }

        // 根据结果生成或随机选择工作ID
        return if(CollUtil.isNotEmpty(luaResultList)){
            WorkIdWrapper(luaResultList!![0], luaResultList[1])
        } else {
            RandomWorkIdChoose().chooseWorkId()
        }
    }

    /**
     * 初始化工作ID选择器
     */
    override fun afterPropertiesSet() {
        chooseAndInit()
    }

}

/**
 * 随机工作ID选择器
 * 该类通过随机数生成工作ID
 */
class RandomWorkIdChoose: AbstractWorkIdChooseTemplate(), InitializingBean {
    /**
     * 选择工作ID
     * @return 返回工作ID封装
     */
    override fun chooseWorkId(): WorkIdWrapper {
        // 生成随机工作ID
        val (start, end) = arrayOf(0, 31)
        return WorkIdWrapper(getRandom(start, end), getRandom(start, end))
    }

    /**
     * 初始化工作ID选择器
     */
    override fun afterPropertiesSet() {
        chooseAndInit()
    }


    companion object {
        /**
         * 生成随机数
         * @param start 起始值
         * @param end 结束值
         * @return 返回随机数
         */
        private fun getRandom(start: Int, end: Int): Long {
            return (Math.random() * (end - start + 1) + start).toLong()
        }
    }
}
























