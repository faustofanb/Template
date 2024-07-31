package fausto.fan.project.framework.starter.log.annotation

// 定义日志记录注解，用于在运行时自动生成日志
@Target(
    AnnotationTarget.CLASS,      // 可以作用于类
    AnnotationTarget.FUNCTION,   // 可以作用于函数
    AnnotationTarget.PROPERTY_GETTER, // 可以作用于属性的getter方法
    AnnotationTarget.PROPERTY_SETTER  // 可以作用于属性的setter方法
)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class ILog(
    /**
     * 入参打印
     *
     * @return 打印结果中是否包含入参，[Boolean.TRUE] 打印，[Boolean.FALSE] 不打印
     */
    val input: Boolean = true,
    /**
     * 出参打印
     *
     * @return 打印结果中是否包含出参，[Boolean.TRUE] 打印，[Boolean.FALSE] 不打印
     */
    val output: Boolean = true
)
