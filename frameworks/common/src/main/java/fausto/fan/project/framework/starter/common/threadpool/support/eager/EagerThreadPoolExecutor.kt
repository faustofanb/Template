package fausto.fan.project.framework.starter.common.threadpool.support.eager

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * EagerThreadPoolExecutor类继承自ThreadPoolExecutor，实现了自定义的线程池。
 * 它主要关注于任务提交的计数和处理拒绝执行的任务。
 *
 * @param corePoolSize 核心线程池大小，即始终维持的最小线程数量。
 * @param maximumPoolSize 最大线程池大小，超过这个数量的线程将会被拒绝执行。
 * @param keepAliveTime 线程的空闲存活时间，当线程池大小超过corePoolSize且线程空闲时，线程会等待这个时间后退出。
 * @param unit keepAliveTime的时间单位。
 * @param workQueue 存储待执行任务的队列，当线程池已满时，新任务将会被加入到这个队列中。
 * @param threadFactory 创建新线程的工厂，用于定制线程的创建过程。
 * @param handler 当线程池和任务队列都满时，用于处理新任务的拒绝策略。
 */
class EagerThreadPoolExecutor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    workQueue: TaskQueue<Runnable>,
    threadFactory: ThreadFactory,
    handler: RejectedExecutionHandler
) : ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler) {

    // 用于记录已提交但尚未执行的任务数量。
    private val submittedTaskCount: AtomicInteger = AtomicInteger(0)

    /**
     * 获取已提交任务的总数。
     * @return 当前已提交任务的数量。
     */
    fun getSubmittedTaskCount(): Int = submittedTaskCount.get()

    /**
     * 在任务执行后进行一些清理工作。
     * 主要用于减少已提交任务的计数。
     * @param r 任务本身。
     * @param t 任务执行过程中抛出的异常（如果有的话）。
     */
    override fun afterExecute(r: Runnable, t: Throwable) {
        submittedTaskCount.decrementAndGet()
    }

    /**
     * 尝试执行给定的任务。
     * 如果执行失败，会尝试将任务重新放入任务队列。
     * 如果队列也满了，则会减少已提交任务的计数并抛出异常。
     * @param command 要执行的任务。
     * @throws RejectedExecutionException 如果任务无法执行。
     */
    override fun execute(command: Runnable) {
        submittedTaskCount.incrementAndGet()
        try {
            super.execute(command)
        } catch (ex: RejectedExecutionException) {
            val taskQueue = super.getQueue() as TaskQueue<Runnable>
            try {
                if (!taskQueue.retryOffer(command, 0L, TimeUnit.MILLISECONDS)) {
                    submittedTaskCount.decrementAndGet()
                    throw RejectedExecutionException("Queue capacity is full $ex")
                }
            } catch (iex: InterruptedException) {
                submittedTaskCount.decrementAndGet()
                throw RejectedExecutionException(iex)
            }
        } catch (ex: Exception) {
            submittedTaskCount.decrementAndGet()
            throw ex
        }
    }

}
