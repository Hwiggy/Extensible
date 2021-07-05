package me.hwiggy.extensible.binding.jvm.classloader

import me.hwiggy.extensible.binding.jvm.contract.JarDescriptor
import me.hwiggy.extensible.binding.jvm.contract.JarExtension
import me.hwiggy.extensible.contract.ExtensionLoader
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

abstract class JarParentClassLoader<D : JarDescriptor, E : JarExtension<D>>(
    parent: ClassLoader
) : ClassLoader(parent), Closeable {
    private val classCache = ConcurrentHashMap<String, Class<*>>()
    abstract val loader: ExtensionLoader<D, E>

    public final override fun findClass(name: String): Class<*> {
        val cached = classCache[name]
        if (cached != null) return cached
        var found: Class<*>? = null
        for (it in loader.getExtensions().map(JarExtension<*>::classLoader)) {
            try {
                found = it.findClass(name, false); break
            } catch (ex: Exception) {
            }
        }
        return found?.also { classCache[name] = it } ?: throw ClassNotFoundException(name)
    }

    fun findClass(
        name: String, loader: JarClassLoader
    ): Class<*> = classCache[name] ?: try {
        loader.findClass(name, false)
    } catch (ex: Exception) {
        null
    }?.also { classCache[name] = it } ?: throw ClassNotFoundException(name)

    final override fun close() {
        loader.close()
        classCache.clear()
    }
}