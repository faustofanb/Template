package fausto.fan.project.framework.starter.designpattern.builder

import java.io.Serializable

/**
 * Builder接口定义了一个构建器模式，用于逐步构建复杂对象。
 *
 * 该接口泛型化，允许构建任何类型的对象T。实现此接口的类应该提供一种方式来逐步配置对象的属性，然后通过调用build方法来创建和返回最终的对象。
 *
 * 实现此接口的类必须实现Serializable接口，以确保构建器对象可以被序列化。这对于例如在不同线程间传递构建器对象，或者将构建器对象保存到磁盘等场景下是必要的。
 *
 * @param <T> 构建器将构建的对象的类型。
 */
interface Builder<T> : Serializable {
    /**
     * 通过调用此方法来构建并返回最终的对象。
     *
     * @return 构建器所构建的对象，其类型为泛型T。
     */
    fun build(): T
}
