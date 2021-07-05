package me.hwiggy.extensible.binding.jvm.contract

import me.hwiggy.extensible.binding.jvm.classloader.JarClassLoader
import me.hwiggy.extensible.binding.jvm.classloader.JarParentClassLoader
import me.hwiggy.extensible.contract.LoadStrategy
import java.io.File

abstract class JarLoadStrategy<D : JarDescriptor, E : JarExtension<D>>(
    private val parent: JarParentClassLoader<D, E>
) : LoadStrategy<D, E>{
    final override fun loadExtension(source: File): E {
        val descriptor = readDescriptor(source)
        val loader = JarClassLoader(parent, source)
        val mainClass = parent.findClass(descriptor.mainClass, loader)
        val module = mainClass.newInstance() as E
        return module.apply {
            this.classLoader = loader
            this.lazySource = loader.file
            this.descriptor = descriptor
        }
    }
}