package fausto.fan.project.framework.starter.base.safa

import org.springframework.beans.factory.InitializingBean

/**
 * FastJsonSafeMode 类，用于在 FastJson 解析 JSON 时启用安全模式，防止潜在的安全风险。
 * 实现了 InitializingBean 接口，确保在 Spring 容器初始化后设置 FastJson 的安全模式。
 */
class FastJsonSafeMode : InitializingBean {

    /**
     * 在所有属性被设置之后执行，设置系统属性使 FastJson 工作在安全模式下。
     */
    override fun afterPropertiesSet() {
        System.setProperty("fastjson2.parser.safeMode", "true")
    }
}

