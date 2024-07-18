package fausto.fan.project.framework.starter.common.threadpool.build

import fausto.fan.project.framework.starter.designpattern.builder.Builder
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * ThreadFactoryBuilder类用于构建自定义的ThreadFactory实例。
 * 它提供了设置线程名前缀、是否为守护线程、线程优先级以及未捕获异常处理器的能力。
 */
class ThreadFactoryBuilder: Builder<ThreadFactory> {

    /**
     * 用于实际创建线程的ThreadFactory备份。
     */
    private var backingThreadFactory: ThreadFactory? = null

    /**
     * 线程名的前缀。
     */
    private var namePrefix: String? = null

    /**
     * 指示线程是否为守护线程的标志。
     */
    private var daemon: Boolean? = null

    /**
     * 线程的优先级。
     */
    private var priority: Int? = null

    /**
     * 用于处理未捕获异常的处理器。
     */
    private var uncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * 设置用于创建线程的实际ThreadFactory。
     * @param threadFactory 负责创建线程的工厂。
     * @return 当前ThreadFactoryBuilder实例。
     */
    fun threadFactory(threadFactory: ThreadFactory): ThreadFactoryBuilder {
        return this.apply { backingThreadFactory = threadFactory }
    }

    /**
     * 设置线程名的前缀。
     * @param namePrefix 线程名的前缀字符串。
     * @return 当前ThreadFactoryBuilder实例。
     */
    fun prefix(namePrefix: String): ThreadFactoryBuilder {
        return this.apply { this.namePrefix = namePrefix }
    }

    /**
     * 设置线程是否为守护线程。
     * @param daemon 如果为true，则创建的线程为守护线程。
     * @return 当前ThreadFactoryBuilder实例。
     */
    fun daemon(daemon: Boolean): ThreadFactoryBuilder {
        return this.apply { this.daemon = daemon }
    }

    /**
     * 设置线程的优先级。
     * @param priority 线程的优先级，必须在[Thread.MIN_PRIORITY]和[Thread.MAX_PRIORITY]之间。
     * @return 当前ThreadFactoryBuilder实例。
     * @throws IllegalArgumentException 如果优先级不在合法范围内。
     */
    fun priority(priority: Int): ThreadFactoryBuilder {
        return when {
            priority < Thread.MIN_PRIORITY -> throw IllegalArgumentException("Priority must be >= Thread.MIN_PRIORITY")
            priority > Thread.MAX_PRIORITY -> throw IllegalArgumentException("Priority must be <= Thread.MAX_PRIORITY")
            else -> this.apply { this.priority = priority }
        }
    }

    /**
     * 设置用于处理未捕获异常的处理器。
     * @param uncaughtExceptionHandler 处理未捕获异常的处理器。
     * @return 当前ThreadFactoryBuilder实例。
     */
    fun uncaughtExceptionHandler(uncaughtExceptionHandler: Thread.UncaughtExceptionHandler): ThreadFactoryBuilder {
        return this.apply { this.uncaughtExceptionHandler = uncaughtExceptionHandler }
    }

    /**
     * 根据当前配置构建ThreadFactory实例。
     * @return 配置后的ThreadFactory实例。
     */
    override fun build(): ThreadFactory {
        return Companion.build(this)
    }

    /**
     * ThreadFactoryBuilder的伴生对象，提供创建ThreadFactoryBuilder实例和构建ThreadFactory的方法。
     */
    companion object {
        /**
         * 串行版本号，用于序列化。
         */
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L

        /**
         * 创建一个新的ThreadFactoryBuilder实例。
         * @return 新的ThreadFactoryBuilder实例。
         */
        fun builder() = ThreadFactoryBuilder()

        /**
         * 根据Builder模式构建ThreadFactory实例。
         * @param builder ThreadFactoryBuilder实例，包含配置信息。
         * @return 配置后的ThreadFactory实例。
         */
        fun build(builder: ThreadFactoryBuilder): ThreadFactory {
            val backingThreadFactory = builder.backingThreadFactory ?: Executors.defaultThreadFactory()
            val namePrefix = builder.namePrefix
            val daemon = builder.daemon
            val priority = builder.priority
            val handler = builder.uncaughtExceptionHandler
            val count: AtomicLong? = namePrefix?.let { AtomicLong() }

            return ThreadFactory { r ->
                val thread = backingThreadFactory.newThread(r).apply {
                    namePrefix?.let { this.name = "${it}_${count?.getAndIncrement()}" }
                    daemon?.let { this.isDaemon = it }
                    priority?.let { this.priority = it }
                    handler?.let { this.uncaughtExceptionHandler = it }
                }
                thread
            }
        }
    }
}
