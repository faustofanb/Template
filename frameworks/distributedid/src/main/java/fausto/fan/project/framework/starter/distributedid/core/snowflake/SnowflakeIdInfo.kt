package fausto.fan.project.framework.starter.distributedid.core.snowflake

/**
 * SnowflakeIdInfo 数据类用于解析 Snowflake 算法生成的 ID 的各部分信息。
 *
 * @property timestamp 时间戳
 * @property workerId 工作机器 ID。
 * @property dataCenterId 数据中心 ID。
 * @property sequence 自增序号，当高频模式下时，同一毫秒内生成 N 个 ID，则这个序号在同一毫秒下，自增以避免 ID 重复
 * @property gene 通过基因法生成的序号，会和 sequence 共占 12 bit
 */
data class SnowflakeIdInfo(
    var timestamp: Long? = null,
    var workerId: Int? = null,
    var dataCenterId: Int? = null,
    var sequence: Int? = null,
    var gene: Int? = null
)

/**
 * 解析 Snowflake 算法生成的 ID。
 *
 * 本函数通过位运算从给定的 Snowflake ID 中提取出时间戳、工作机器 ID、数据中心 ID 和序列号。
 * 这些信息对于理解和跟踪 ID 的生成来源非常有用。
 *
 * @param snowflakeId Snowflake 算法生成的 Long 类型 ID。
 * @return 返回一个 SnowflakeIdInfo 对象，包含解析出的各部分信息。
 */
fun Snowflake.parseSnowflakeId(snowflakeId: Long): SnowflakeIdInfo {

    // 定义一个 lambda 表达式，用于获取 Snowflake 类中定义的字段值。
    val getField = { name: String ->
        Snowflake::class.java.getDeclaredField(name)
            .apply { isAccessible = true }
            .getLong(this)
    }

    // 创建一个 SnowflakeIdInfo 对象，并解析 snowflakeId 的各个部分。
    return SnowflakeIdInfo().apply {
        // 解析出序列号
        sequence = (snowflakeId and (-1L shl getField("SEQUENCE_BITS").toInt()).inv()).toInt()
        // 解析出工作机器 ID
        workerId = (
                (snowflakeId shr getField("WORKER_ID_SHIFT").toInt())
                        and
                        (-1L shl getField("WORKER_ID_BITS").toInt()).inv()
                ).toInt()
        // 解析出数据中心 ID
        dataCenterId = (
                (snowflakeId shr getField("DATA_CENTER_ID_SHIFT").toInt())
                        and
                        (-1L shl getField("DATA_CENTER_ID_BITS").toInt()).inv()
                ).toInt()
        // 解析出时间戳，并加上起始时间戳（twepoch），得到绝对时间
        timestamp = (snowflakeId shr getField("TIMESTAMP_LEFT_SHIFT").toInt()) + getField("twepoch")
    }
}




