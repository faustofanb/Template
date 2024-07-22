package fausto.fan.project.framework.starter.common.threadpool.proxy

import fausto.fan.project.framework.starter.base.Slf4j
import fausto.fan.project.framework.starter.base.Slf4j.Companion.log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.atomic.AtomicLong

// 使用@Slf4j注解，以便在类中直接使用日志功能
@Slf4j
/**
 * RejectedProxyInvocationHandler是一个InvocationHandler实现，用于处理代理对象的调用。
 * 它特别用于处理线程池执行拒绝的情况，此时会增加拒绝计数，并尝试执行相应的方法。
 *
 * @param target 被代理的对象，调用的方法将作用于这个目标对象。
 * @param rejectCount 一个原子长整型变量，用于记录拒绝执行的次数。
 */
class RejectedProxyInvocationHandler(
    private val target: Any,
    private val rejectCount: AtomicLong
): InvocationHandler {

    /**
     * 当调用代理对象的方法时，此方法会被执行。
     * 它的主要逻辑是增加拒绝计数，尝试执行目标方法，并处理可能的InvocationTargetException。
     *
     * @param proxy 代理对象本身。
     * @param method 被调用的方法。
     * @param args 方法的参数。
     * @return 方法的返回值。
     * @throws Throwable 如果目标方法抛出异常，则会将异常传播。
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any {
        // 拒绝执行次数加一
        rejectCount.incrementAndGet()
        try {
            // 记录线程池执行拒绝策略的日志，模拟报警机制
            log.error("线程池执行拒绝策略, 此处模拟报警...")
            // 调用目标对象的相应方法
            return method.invoke(target, *args)
        } catch (ex: InvocationTargetException) {
            // 将目标异常抛出，如果目标异常为空，则抛出InvocationTargetException本身
            throw ex.cause ?: ex
        }
    }
}

/**
 * RejectedProxyUtil对象用于创建一个代理实例，该实例用于在拒绝执行策略中提供额外的定制行为。
 * 通过使用动态代理，它可以包装一个已有的RejectedExecutionHandler实例，并在执行被拒绝时提供自定义的处理逻辑。
 */
object RejectedProxyUtil {

    /**
     * 创建一个定制的RejectedExecutionHandler代理实例。
     *
     * @param rejectedExecutionHandler 原始的RejectedExecutionHandler实例，将被代理对象包装。
     * @param rejectNum 用于记录拒绝执行次数的原子长整型变量，代理对象将利用它来执行特定的逻辑。
     * @return 返回一个RejectedExecutionHandler代理实例，它在执行被拒绝时会表现出定制的行为。
     */
    fun createProxy(rejectedExecutionHandler: RejectedExecutionHandler, rejectNum: AtomicLong): RejectedExecutionHandler {
        // 使用Java的动态代理机制创建一个新的代理实例
        // 代理实例将实现RejectedExecutionHandler接口，并在调用方法时执行RejectedProxyInvocationHandler中定义的逻辑
        return Proxy.newProxyInstance(
            rejectedExecutionHandler.javaClass.classLoader,
            rejectedExecutionHandler.javaClass.interfaces,
            RejectedProxyInvocationHandler(rejectedExecutionHandler, rejectNum) // 实现InvocationHandler接口的逻辑类
        ) as RejectedExecutionHandler
    }
}

//fun main() {
//    val abortPolicy = AbortPolicy()
//    val rejectNum = AtomicLong()
//    val executor = ThreadPoolExecutor(
//        1,
//        3,
//        1000,
//        TimeUnit.MILLISECONDS,
//        LinkedBlockingQueue(1),
//        RejectedProxyUtil.createProxy(abortPolicy, rejectNum)
//    )
//    for (i in 0..4) {
//        try {
//            executor.execute {
//                println("$i -> 执行任务")
//                Thread.sleep(100000L)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//    println("========> ThreadPool rejected execute num: $rejectNum")
//    executor.shutdown()
//    exitProcess(0)
//}