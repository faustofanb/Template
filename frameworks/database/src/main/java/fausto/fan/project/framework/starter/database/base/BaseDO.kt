package fausto.fan.project.framework.starter.database.base

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.TableField
import java.util.*

/**
 * 数据库实体基类，提供公共字段和方法
 */
data class BaseDO(
    // 创建时间，自动填充为插入时的当前时间
    @TableField(fill = FieldFill.INSERT)
    var createTime: Date? = null,

    // 更新时间，自动填充为每次更新时的当前时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updateTime: Date? = null,

    // 删除标记，用于逻辑删除，默认为未删除状态
    @TableField(fill = FieldFill.INSERT)
    var delFlag: Int = 0
)

