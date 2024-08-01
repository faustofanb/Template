package fausto.fan.project.framework.starter.distributedid.core.snowflake

/**
 * 数据类 WorkIdWrapper 用于封装工作ID和数据中心ID。
 *
 * 该类的主要目的是在业务逻辑中同时跟踪工作ID和数据中心ID，
 * 提供一种便捷的方式来管理和使用这两个相关的ID值。
 *
 * @property workId 长整型的工作ID，可能为null。这个ID通常用于标识特定的工作单元或任务。
 * @property dataCenterId 长整型的数据中心ID，可能为null。这个ID用于标识数据中心，对于分布式系统来说非常有用。
 */
data class WorkIdWrapper(
    var workId: Long? = null,
    var dataCenterId: Long? = null
)

