package fausto.fan.project.framework.starter.common.threadpool.support.eager

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

/**
 * 任务队列，用于管理任务的提交。
 * 该队列与一个特定的线程池（EagerThreadPoolExecutor）关联，用于控制任务的提交速率，避免过度提交。
 *
 * @param capacity 队列的容量，限制同时等待执行的任务数量。
 * @param executor 关联的执行线程池，用于执行队列中的任务。
 */
class TaskQueue<R: Runnable>(
    private val capacity: Int,
    private val executor: EagerThreadPoolExecutor
): LinkedBlockingQueue<R>(capacity) {

    /**
     * 尝试将一个任务添加到队列中。
     * 如果线程池已达到最大大小，或者已提交的任务数量未达到当前线程池大小，则任务将被正常添加到队列中。
     * 这种策略旨在控制任务的提交速率，避免过度提交。
     *
     * @param e 要添加到队列的任务。
     * @return 如果任务被成功添加到队列中，则返回true；否则返回false。
     */
    override fun offer(e: R): Boolean {
        val currentPoolThreadSize = executor.poolSize

        // 如果有核心线程正在空闲，将任务加入阻塞队列，由核心线程进行处理任务
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) return super.offer(e)

        // 当前线程池线程数量小于最大线程数，返回 False，根据线程池源码，会创建非核心线程
        if(currentPoolThreadSize < executor.maximumPoolSize) return false

        // 如果当前线程池数量大于最大线程数，任务加入阻塞队列
        return super.offer(e)
    }

    /**
     * 尝试在指定的超时时间内将任务添加到队列中。
     * 如果线程池已关闭，则抛出RejectedExecutionException异常。
     * 这个方法允许在等待一定时间后再次尝试添加任务，用于处理任务提交的阻塞情况。
     *
     * @param o 要添加到队列的任务。
     * @param timeout 超时时间。
     * @param unit 超时时间的单位。
     * @return 如果任务被成功添加到队列中，则返回true；否则返回false。
     * @throws RejectedExecutionException 如果线程池已关闭。
     */
    fun retryOffer(o: R, timeout: Long, unit: TimeUnit): Boolean {
        if(executor.isShutdown) throw RejectedExecutionException("Executor already shutdown")
        return super.offer(o, timeout, unit)
    }
}
