package fausto.fan.project.framework.starter.idempotent.enums

/**
 * 定义消息消费状态的枚举类
 */
enum class IdempotentMQConsumeStatusEnum(val code: String) {
    // 消息正在被消费的状态
    CONSUMING("0"),
    // 消费完成的状态
    CONSUMED("1");

    companion object {
        /**
         * 判断消息消费状态是否为正在消费
         *
         * @param consumeStatus 消费状态字符串
         * @return 如果是正在消费状态则返回true，否则返回false
         */
        fun isError(consumeStatus: String): Boolean {
            return CONSUMING.code == consumeStatus
        }
    }
}

/**
 * 枚举类 IdempotentSceneEnum 定义了两种幂等性场景：RESTAPI 和 MQ。
 * 幂等性是指一个操作一次和多次执行的效果是一样的，这在处理网络请求或消息队列场景下尤为重要。
 * 通过定义这样的枚举，可以在代码中清晰地指定和区分不同的场景，以更好地实现幂等性控制逻辑。
 */
enum class IdempotentSceneEnum {
    RESTAPI, // RESTAPI 场景，表示通过 RESTFUL API 接口进行的操作。
    MQ // MQ 场景，表示通过消息队列进行的操作。
}

/**
 * 枚举类，表示幂等性操作的类型
 * 幂等性操作是指一次操作产生的效果与多次操作相同，不会因为操作的重复执行而产生不同的结果
 * 在这个枚举类中，定义了三种幂等性操作的类型：TOKEN, PARAM, 和 SPEL
 */
enum class IdempotentTypeEnum {
    // 表示使用令牌（Token）作为幂等性操作的关键类型
    TOKEN,
    // 表示使用参数（Param）作为幂等性操作的关键类型
    PARAM,
    // 表示使用表达式语言（Spring Expression Language, SPEL）作为幂等性操作的关键类型
    SPEL
}
