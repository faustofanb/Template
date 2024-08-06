package fausto.fan.project.framework.starter.database.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import fausto.fan.project.framework.starter.database.handler.CustomIdGenerator
import fausto.fan.project.framework.starter.database.handler.MyMetaObjectHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * MybatisPlus 自动配置类
 * 该类负责配置 MybatisPlus 相关的 Bean，以实现自动化配置
 */
class MybatisPlusAutoConfiguration {

    /**
     * 配置 MybatisPlus 拦截器
     * 拦截器主要用于拦截 SQL 执行过程，以实现如分页、日志记录等功能
     *
     * @return MybatisPlusInterceptor 配置好的 MybatisPlus 拦截器实例
     */
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        return MybatisPlusInterceptor().apply {
            // 添加分页拦截器，指定数据库类型为 MySQL
            addInnerInterceptor(PaginationInnerInterceptor(DbType.MYSQL))
        }
    }

    /**
     * 配置 MyMetaObjectHandler
     * MyMetaObjectHandler 主要用于处理元对象，实现字段的自动填充等功能
     *
     * @return MyMetaObjectHandler 配置好的 MyMetaObjectHandler 实例
     */
    @Bean
    fun myMetaObjectHandler(): MyMetaObjectHandler = MyMetaObjectHandler()

    /**
     * 配置自定义的 ID 生成器
     * 在多线程环境下，使用 @Primary 注解保证仅使用一个 Bean 实例，避免冲突
     *
     * @return IdentifierGenerator 自定义的 ID 生成器实例
     */
    @Bean
    @Primary
    fun idGenerator(): IdentifierGenerator = CustomIdGenerator()
}
