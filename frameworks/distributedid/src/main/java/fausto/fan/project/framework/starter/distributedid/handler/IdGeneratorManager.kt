package fausto.fan.project.framework.starter.distributedid.handler

import fausto.fan.project.framework.starter.distributedid.core.IdGenerator
import fausto.fan.project.framework.starter.distributedid.core.serviceid.DefaultServiceIdGenerator
import fausto.fan.project.framework.starter.distributedid.core.serviceid.ServiceIdGenerator
import java.util.concurrent.ConcurrentHashMap

/**
 * Id生成器管理器对象，用于注册和获取Id生成器
 */
object IdGeneratorManager {

    // 存储各个资源对应的Id生成器
    private val MANAGER = ConcurrentHashMap<String, IdGenerator>()

    // 初始化时，注册默认的Id生成器
    init {
        MANAGER["default"] = DefaultServiceIdGenerator()
    }

    /**
     * 注册一个新的Id生成器
     *
     * @param resource 资源名称，用于标识Id生成器
     * @param idGenerator 要注册的Id生成器
     */
    fun registerIdGenerator(resource: String, idGenerator: IdGenerator) {
        // 如果该资源已经存在Id生成器，则不进行操作
        MANAGER[resource]?.let {
            return
        } ?: {
            // 否则，将新的Id生成器注册到MANAGER中
            MANAGER[resource] = idGenerator
        }
    }

    /**
     * 获取指定资源的Id生成器
     *
     * @param resource 资源名称
     * @return 对应的ServiceIdGenerator实例，如果不存在则返回null
     */
    fun getIdGenerator(resource: String): ServiceIdGenerator? {
        // 从MANAGER中获取对应资源的Id生成器，并将其转换为ServiceIdGenerator类型
        return MANAGER[resource]?.let {
            it as ServiceIdGenerator
        }
    }

    /**
     * 获取默认的ServiceId生成器
     *
     * @return 默认的ServiceIdGenerator实例，如果不存在则返回null
     */
    fun getDefaultServiceIdGenerator(): ServiceIdGenerator? {
        // 从MANAGER中获取默认的Id生成器，并将其转换为ServiceIdGenerator类型
        return MANAGER["default"]?.let {
            it as ServiceIdGenerator
        }
    }
}
