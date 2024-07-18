package fausto.fan.project.framework.starter.common.enums

/**
 * 定义了文档的状态枚举，包括正常和删除两种状态。
 *
 * 枚举类DelEnum用于表示文档的状态，通过statusCode属性区分不同的状态。
 * 每个枚举常量代表一种特定的状态，statusCode的值对应于该状态的系统代码。
 */
enum class DelEnum(val statusCode: Int) {
    /**
     * 文档正常状态。
     * statusCode为0，表示文档处于正常状态，没有被删除。
     */
    NORMAL(0),

    /**
     * 文档删除状态。
     * statusCode为1，表示文档已被删除。
     */
    DELETE(1)
}

/**
 * 枚举类FlagEnum定义了两种状态：FALSE和TRUE，每种状态对应一个整数值。
 *
 */
enum class FlagEnum(val flag: Int) {
    FALSE(0), TRUE(1)
}

/**
 * 定义操作类型的枚举类。
 *
 * 该枚举类包含了三种操作类型：保存（SAVE）、更新（UPDATE）和删除（DELETE）。
 * 这些操作类型可以用于标识对数据进行的操作，以便在应用程序中进行相应的处理。
 */
enum class OperationEnum{
    SAVE, UPDATE, DELETE
}

/**
 * 表示操作状态的枚举类。
 *
 * 该枚举类定义了两种操作状态：成功和失败，以及它们对应的状态码。
 * 状态码是一个整数，用于在程序中以紧凑的方式表示操作的结果状态。
 */
enum class StatusEnum(val statusCode: Int) {
    /**
     * 操作成功的状态。
     * 状态码为0，通常表示没有错误发生，操作完成。
     */
    SUCCESS(0),

    /**
     * 操作失败的状态。
     * 状态码为1，通常表示操作过程中发生了错误或异常。
     */
    FAIL(1)
}
