package fausto.fan.project.framework.starter.distributedid.toolkit

import fausto.fan.project.framework.starter.distributedid.core.snowflake.Snowflake
import fausto.fan.project.framework.starter.distributedid.core.snowflake.SnowflakeIdInfo
import fausto.fan.project.framework.starter.distributedid.core.snowflake.parseSnowflakeId
import fausto.fan.project.framework.starter.distributedid.handler.IdGeneratorManager


object SnowflakeIdUtil {

    /**
     * 雪花算法对象
     */
    private lateinit var SNOWFLAKE: Snowflake

    /**
     * 初始化雪花算法
     */
    fun initSnowflake(snowflake: Snowflake) {
        SNOWFLAKE = snowflake
    }

    /**
     * 获取雪花算法实例
     */
    fun getInstance(): Snowflake {
        return SNOWFLAKE
    }

    /**
     * 获取雪花算法下一个 ID
     */
    fun nextId(): Long {
        return SNOWFLAKE.nextId()
    }

    /**
     * 获取雪花算法下一个字符串类型 ID
     */
    fun nextIdStr(): String {
        return nextId().toString()
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    fun parseSnowflakeId(snowflakeId: String): SnowflakeIdInfo {
        return SNOWFLAKE.parseSnowflakeId(snowflakeId.toLong())
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    fun parseSnowflakeId(snowflakeId: Long): SnowflakeIdInfo {
        return SNOWFLAKE.parseSnowflakeId(snowflakeId)
    }

    /**
     * 根据 {@param serviceId} 生成雪花算法 ID
     */
    fun nextIdByService(serviceId: String): Long {
        return IdGeneratorManager.getDefaultServiceIdGenerator()!!.nextId(serviceId.toLong())
    }

    /**
     * 根据 {@param serviceId} 生成字符串类型雪花算法 ID
     */
    fun nextIdStrByService(serviceId: String): String {
        return IdGeneratorManager.getDefaultServiceIdGenerator()!!.nextIdStr(serviceId.toLong())
    }

    /**
     * 根据 {@param serviceId} 生成字符串类型雪花算法 ID
     */
    fun nextIdStrByService(resource: String, serviceId: Long): String {
        return IdGeneratorManager.getIdGenerator(resource)!!.nextIdStr(serviceId)
    }

    /**
     * 根据 {@param serviceId} 生成字符串类型雪花算法 ID
     */
    fun nextIdStrByService(resource: String, serviceId: String): String {
        return IdGeneratorManager.getIdGenerator(resource)!!.nextIdStr(serviceId.toLong())
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    fun parseSnowflakeServiceId(snowflakeId: String): SnowflakeIdInfo {
        return IdGeneratorManager.getDefaultServiceIdGenerator()!!.parseSnowflakeId(snowflakeId.toLong())
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    fun parseSnowflakeServiceId(resource: String, snowflakeId: String): SnowflakeIdInfo {
        return IdGeneratorManager.getIdGenerator(resource)!!.parseSnowflakeId(snowflakeId.toLong())
    }
}