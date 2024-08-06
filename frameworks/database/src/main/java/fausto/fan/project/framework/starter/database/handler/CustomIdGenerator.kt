package fausto.fan.project.framework.starter.database.handler

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator
import fausto.fan.project.framework.starter.distributedid.toolkit.SnowflakeIdUtil

/**
 * CustomIdGenerator 类，继承自 IdentifierGenerator 接口
 * 该类用于生成实体的唯一标识符
 */
class CustomIdGenerator: IdentifierGenerator {
    /**
     * 生成下一个唯一标识符
     *
     * @param entity 生成标识符的实体对象，该参数在本实现中未使用，因为 Snowflake 算法是无状态的
     * @return 生成的唯一标识符，类型为 Number
     *
     * 使用 SnowflakeIdUtil.nextId() 方法生成唯一标识符，这是因为 Snowflake 算法可以高效地生成全局唯一 ID，
     * 同时具有高吞吐量和低重复率的特点，非常适合用于分布式系统中
     */
    override fun nextId(entity: Any): Number {
        return SnowflakeIdUtil.nextId()
    }
}
