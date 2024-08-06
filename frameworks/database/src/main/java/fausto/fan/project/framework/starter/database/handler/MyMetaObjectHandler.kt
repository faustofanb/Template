package fausto.fan.project.framework.starter.database.handler

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import fausto.fan.project.framework.starter.common.enums.DelEnum
import org.apache.ibatis.reflection.MetaObject
import java.util.*

/**
 * 自定义元对象处理器，用于处理对象的元数据操作
 * 主要用于初始化和更新对象的特定字段
 */
class MyMetaObjectHandler: MetaObjectHandler {

    /**
     * 在插入对象之前，填充特定的字段
     * 该方法主要用于初始化对象的创建时间、更新时间和删除标志
     *
     * @param metaObject 待填充元数据对象的MetaObject表示
     */
    override fun insertFill(metaObject: MetaObject) {
        // 填充创建时间为当前时间
        this.strictInsertFill(metaObject, "createTime", Date::class.java, Date())
        // 填充更新时间为当前时间
        this.strictInsertFill(metaObject, "updateTime", Date::class.java, Date())
        // 填充删除标志为正常状态
        this.strictInsertFill(metaObject, "delFlag", Int::class.java, DelEnum.NORMAL.statusCode)
    }

    /**
     * 在更新对象之前，填充特定的字段
     * 该方法主要用于更新对象的更新时间
     *
     * @param metaObject 待填充元数据对象的MetaObject表示
     */
    override fun updateFill(metaObject: MetaObject) {
        // 更新更新时间为当前时间
        this.strictInsertFill(metaObject, "updateTime", Date::class.java, Date())
    }
}
