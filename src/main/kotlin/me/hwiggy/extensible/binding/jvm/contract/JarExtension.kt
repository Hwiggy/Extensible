package me.hwiggy.extensible.binding.jvm.contract

import me.hwiggy.extensible.binding.jvm.classloader.JarClassLoader
import me.hwiggy.extensible.contract.Extension
import java.io.File

abstract class JarExtension<D : JarDescriptor> : Extension {
    lateinit var classLoader: JarClassLoader
        internal set

    lateinit var descriptor: D
        internal set

    lateinit var lazySource: File
        internal set

    final override val sourceFile: File
        get() = lazySource
}