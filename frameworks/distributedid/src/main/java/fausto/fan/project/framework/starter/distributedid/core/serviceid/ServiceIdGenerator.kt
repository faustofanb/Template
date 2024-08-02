package fausto.fan.project.framework.starter.distributedid.core.serviceid

import fausto.fan.project.framework.starter.distributedid.core.IdGenerator
import fausto.fan.project.framework.starter.distributedid.core.snowflake.SnowflakeIdInfo
import fausto.fan.project.framework.starter.distributedid.toolkit.SnowflakeIdUtil
import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * 服务ID生成器接口，继承自ID生成器
 */
interface ServiceIdGenerator: IdGenerator {
    /**
     * 生成下一个服务ID
     * @param serviceId 服务ID
     * @return 生成的ID
     */
    fun nextId(serviceId: Long): Long = 0L

    /**
     * 生成下一个服务ID的字符串形式
     * @param serviceId 服务ID
     * @return 生成的ID字符串
     */
    fun nextIdStr(serviceId: Long): String = ""

    /**
     * 解析Snowflake ID
     * @param snowflakeId Snowflake ID
     * @return Snowflake ID信息
     */
    fun parseSnowflakeId(snowflakeId: Long): SnowflakeIdInfo
}

/**
 * 默认服务ID生成器实现
 */
class DefaultServiceIdGenerator: ServiceIdGenerator {

    /**
     * ID生成器实例，使用Snowflake算法
     */
    private val idGenerator: IdGenerator = SnowflakeIdUtil.getInstance()

    /**
     * 最大业务ID位长度
     */
    private var maxBizIdBitLen: Long = 0L

    /**
     * 构造函数，指定服务ID位长度
     * @param serviceIdBitLen 服务ID位长度
     */
    constructor(serviceIdBitLen: Long) {
        this.maxBizIdBitLen = 2.0.pow(serviceIdBitLen.toDouble()).toLong()
    }

    /**
     * 默认构造函数，使用默认序列业务位
     */
    constructor(): this(SEQUENCE_BIZ_BITS)

    /**
     * 生成下一个服务ID
     * @param serviceId 服务ID
     * @return 生成的ID
     */
    override fun nextId(serviceId: Long): Long {
        return (serviceId.hashCode().absoluteValue % maxBizIdBitLen) or idGenerator.nextId()
    }

    /**
     * 生成下一个服务ID的字符串形式
     * @param serviceId 服务ID
     * @return 生成的ID字符串
     */
    override fun nextIdStr(serviceId: Long): String {
        return nextId(serviceId).toString()
    }

    /**
     * 解析Snowflake ID
     * @param snowflakeId Snowflake ID
     * @return Snowflake ID信息
     */
    override fun parseSnowflakeId(snowflakeId: Long): SnowflakeIdInfo {
        return SnowflakeIdInfo().apply {
            sequence = ((snowflakeId shr SEQUENCE_BIZ_BITS.toInt()) and (-1L shl SEQUENCE_ACTUAL_BITS.toInt()).inv()).toInt()
            workerId = (
                    (snowflakeId shr WORKER_ID_SHIFT.toInt())
                        and
                    (-1L shl WORKER_ID_BITS.toInt()).inv()
            ).toInt()
            dataCenterId = (
                    (snowflakeId shr DATA_CENTER_ID_SHIFT.toInt())
                        and
                    (-1L shl DATA_CENTER_ID_BITS.toInt()).inv()
            ).toInt()
            timestamp = (snowflakeId shr TIMESTAMP_LEFT_SHIFT.toInt()) + DEFAULT_TWEPOCH
            gene = (snowflakeId and (-1L shl SEQUENCE_BIZ_BITS.toInt()).inv()).toInt()
        }
    }

    /**
     * 伴生对象，定义常量和配置
     */
    companion object {
        /**
         * 工作 ID 5 bit
         */
        private const val WORKER_ID_BITS: Long = 5L

        /**
         * 数据中心 ID 5 bit
         */
        private const val DATA_CENTER_ID_BITS: Long = 5L

        /**
         * 序列号 12 位，表示只允许 workerId 的范围为 0-4095
         */
        private const val SEQUENCE_BITS: Long = 12L

        /**
         * 真实序列号 bit
         */
        private const val SEQUENCE_ACTUAL_BITS: Long = 8L

        /**
         * 基因 bit
         */
        private const val SEQUENCE_BIZ_BITS: Long = 4L

        /**
         * 机器节点左移12位
         */
        private const val WORKER_ID_SHIFT: Long = SEQUENCE_BITS

        /**
         * 默认开始时间
         */
        private var DEFAULT_TWEPOCH: Long = 1288834974657L

        /**
         * 数据中心节点左移 17 位
         */
        private const val DATA_CENTER_ID_SHIFT: Long = SEQUENCE_BITS + WORKER_ID_BITS

        /**
         * 时间毫秒数左移 22 位
         */
        private const val TIMESTAMP_LEFT_SHIFT: Long = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS
    }
}
