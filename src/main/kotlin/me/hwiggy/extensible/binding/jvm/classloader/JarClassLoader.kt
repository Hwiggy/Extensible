package me.hwiggy.extensible.binding.jvm.classloader

import me.hwiggy.extensible.binding.jvm.contract.JarDescriptor
import me.hwiggy.extensible.binding.jvm.contract.JarExtension
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

class JarClassLoader(
    private val ancestor: JarParentClassLoader<out JarDescriptor, out JarExtension<out JarDescriptor>>, val file: File
) : ClassLoader(ancestor), Closeable {
    private val jar = JarFile(file)
    private val classCache = ConcurrentHashMap<String, Class<*>>()

    override fun findClass(name: String): Class<*> = findClass(name, true)

    fun findClass(
        name: String,
        searchParent: Boolean
    ): Class<*> = classCache[name] ?: try {
        val className = name.replace(".", "/").plus(".class")
        jar.getJarEntry(className)?.let {
            jar.getInputStream(it)?.use(InputStream::readBytes)
        }?.let { defineClass(name, it, 0, it.size) }
    } catch (ex: Exception) {
        try {
            if (searchParent) ancestor.findClass(name) else null
        } catch (ex: Exception) {
            null
        }
    }?.also { classCache[name] = it } ?: throw ClassNotFoundException(name)

    override fun getResourceAsStream(name: String): InputStream? =
        jar.getJarEntry(name)?.let(jar::getInputStream)

    override fun findResource(
        name: String?
    ): URL? {
        if (name == null) return null
        if (jar.getEntry(name) == null) return null
        return URL("jar:file:${file.absolutePath}!/$name")
    }

    override fun findResources(
        name: String?
    ): Enumeration<URL> {
        val found = findResource(name) ?: return Collections.emptyEnumeration()
        return Collections.enumeration(mutableSetOf(found))
    }

    override fun close() {
        jar.close()
        classCache.clear()
    }
}