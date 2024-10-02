package fausto.fan.project.framework.starter.idempotent.toolkit

import cn.hutool.core.util.ArrayUtil
import com.google.common.collect.Lists
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import java.lang.reflect.Method

/**
 * LogUtil对象提供了一种获取Logger实例的方法
 * 这个对象的目的是统一日志记录方式，提供了一种简便的方法来获取Logger实例
 */
object LogUtil {
    /**
     * 根据方法签名获取Logger实例
     *
     * @param joinPoint 切入点对象，包含了方法签名等信息
     * @return 返回对应的Logger实例，用于日志记录
     *
     * 方法解释：
     * 1. 使用LogFactory的getLogger方法获取Logger实例
     * 2. 传入参数为方法签名对象，表示需要为哪个类获取Logger
     * 3. 如果签名对象不是MethodSignature类型，将抛出ClassCastException
     */
    fun getLog(joinPoint: ProceedingJoinPoint): Logger {
        return LoggerFactory.getLogger((joinPoint.signature as MethodSignature).declaringType)
    }
}


object SpELUtil {
    /**
     * 校验并返回实际使用的 spEL 表达式
     *
     * @param spEl spEL 表达式
     * @return 实际使用的 spEL 表达式
     */
    fun parseKey(spEl: String, method: Method, contextObj: Array<Any?>): Any? {
        val spELFlag = Lists.newArrayList("#", "T(")
        val optional = spELFlag.stream().filter { s: String -> spEl.contains(s) }.findFirst()
        if (optional.isPresent) {
            return parse(spEl, method, contextObj)
        }
        return spEl
    }

    /**
     * 转换参数为字符串
     *
     * @param spEl       spEl 表达式
     * @param contextObj 上下文对象
     * @return 解析的字符串值
     */
    fun parse(spEl: String, method: Method, contextObj: Array<Any?>): Any? {
        val discoverer = DefaultParameterNameDiscoverer()
        val parser: ExpressionParser = SpelExpressionParser()
        val exp = parser.parseExpression(spEl)
        val params = discoverer.getParameterNames(method)
        val context = StandardEvaluationContext()
        if (ArrayUtil.isNotEmpty(params)) {
            for (len in params!!.indices) {
                context.setVariable(params[len], contextObj[len])
            }
        }
        return exp.getValue(context)
    }
}