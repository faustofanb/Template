package fausto.fan.project.framework.starter.common.threadpool.build

import fausto.fan.project.framework.starter.designpattern.builder.Builder
import java.math.BigDecimal
import java.util.concurrent.*
/**
 * 线程池构造器类，用于通过一系列配置选项构建线程池。
 */
class ThreadPoolBuilder : Builder<ThreadPoolExecutor> {

    /**
     * 核心线程池大小，默认为计算得到的处理器核心数的一定比例。
     */
    private var corePoolSize: Int = calculateCoreNum()

    /**
     * 最大线程池大小，默认为核心线程数的1.5倍。
     */
    private var maximumPoolSize: Int = corePoolSize + (corePoolSize ushr 1)

    /**
     * 线程保持活跃时间，默认为30秒。
     */
    private var keepAliveTime: Long = 30000L

    /**
     * 时间单位，默认为毫秒。
     */
    private var timeUnit: TimeUnit = TimeUnit.MILLISECONDS

    /**
     * 工作队列，默认为容量为4096的链接阻塞队列。
     */
    private var workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(4096)

    /**
     * 拒绝策略，默认为AbortPolicy，即抛出RejectedExecutionException。
     */
    private var rejectedExecutionHandler: RejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy()

    /**
     * 是否为守护线程，默认为false。
     */
    private var isDaemon: Boolean = false

    /**
     * 线程名前缀，默认为空。
     */
    private var threadNamePrefix: String? = null

    /**
     * 线程工厂，默认为空。
     */
    private var threadFactory: ThreadFactory? = null

    /**
     * 计算合理的默认核心线程数。
     * 通过将处理器核心数除以0.2来估算，认为每个核心可以同时处理5个线程。
     * @return 核心线程数的估算值。
     */
    private fun calculateCoreNum(): Int {
        return BigDecimal(Runtime.getRuntime().availableProcessors())
            .divide(BigDecimal("0.2"))
            .toInt()
    }

    /**
     * 设置线程工厂。
     * @param threadFactory 线程工厂实例。
     * @return 当前线程池构造器实例。
     */
    fun threadFactory(threadFactory: ThreadFactory): ThreadPoolBuilder {
        return this.apply { this.threadFactory = threadFactory }
    }

    /**
     * 设置核心线程池大小。
     * @param corePoolSize 核心线程池大小。
     * @return 当前线程池构造器实例。
     */
    fun corePoolSize(corePoolSize: Int): ThreadPoolBuilder {
        return this.apply { this.corePoolSize = corePoolSize }
    }

    /**
     * 设置最大线程池大小。
     * 如果设置的最大值小于核心线程数，则将核心线程数调整为最大值。
     * @param maximumPoolSize 最大线程池大小。
     * @return 当前线程池构造器实例。
     */
    fun maximumPoolSize(maximumPoolSize: Int): ThreadPoolBuilder {
        this.maximumPoolSize = maximumPoolSize
        if(maximumPoolSize < this.corePoolSize) this.corePoolSize = maximumPoolSize
        return this
    }

    /**
     * 设置线程名前缀和守护线程状态。
     * @param threadNamePrefix 线程名前缀。
     * @param isDaemon 是否为守护线程。
     * @return 当前线程池构造器实例。
     */
    fun threadFactory(threadNamePrefix: String, isDaemon: Boolean): ThreadPoolBuilder {
        return this.apply { this.threadNamePrefix = threadNamePrefix; this.isDaemon = isDaemon }
    }

    /**
     * 设置线程保持活跃时间及其时间单位。
     * @param keepAliveTime 线程保持活跃时间。
     * @param timeUnit 时间单位。
     * @return 当前线程池构造器实例。
     */
    fun keepAliveTime(keepAliveTime: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): ThreadPoolBuilder {
        return this.apply { this.keepAliveTime = keepAliveTime ; this.timeUnit = timeUnit}
    }

    /**
     * 设置拒绝策略。
     * @param rejectedExecutionHandler 拒绝策略实例。
     * @return 当前线程池构造器实例。
     */
    fun rejected(rejectedExecutionHandler: RejectedExecutionHandler): ThreadPoolBuilder {
        return this.apply { this.rejectedExecutionHandler = rejectedExecutionHandler }
    }

    /**
     * 设置工作队列。
     * @param workQueue 工作队列实例。
     * @return 当前线程池构造器实例。
     */
    fun workQueue(workQueue: BlockingQueue<Runnable>): ThreadPoolBuilder {
        return this.apply { this.workQueue = workQueue }
    }

    /**
     * 根据当前配置构建线程池。
     * 如果未显式设置线程工厂，则根据线程名前缀和守护线程状态创建默认的线程工厂。
     * @return 配置后的线程池实例。
     */
    override fun build(): ThreadPoolExecutor {
        if (threadFactory == null) {
            assert(threadNamePrefix != null){"The thread name prefix cannot be empty or an empty string."}
            threadFactory = ThreadFactoryBuilder.builder().prefix(threadNamePrefix!!).daemon(isDaemon).build()
        }
        val executorService: ThreadPoolExecutor
        try {
            executorService = ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue,
                threadFactory!!,
                rejectedExecutionHandler
            )
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Error creating thread pool parameter.")
        }
        return executorService
    }

    /**
     * 创建一个新的ThreadPoolBuilder实例。
     * @return ThreadPoolBuilder实例。
     */
    companion object {
        fun builder(): ThreadPoolBuilder {return ThreadPoolBuilder()}
    }
}
