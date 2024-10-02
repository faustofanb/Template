package fausto.fan.project.framework.starter.idempotent.core

/**
 * 自定义异常类，用于表示重复消费异常
 *
 * 当需要标记某个操作因为重复消费而产生错误时，可以抛出此异常
 *
 * @param error Boolean类型，表示是否存在错误。这里用于指示是否因为重复消费而导致错误
 */
class RepeatConsumptionException(val error: Boolean): RuntimeException()
