package me.hwiggy.extensible.binding.jvm.contract

import me.hwiggy.extensible.binding.jvm.classloader.JarClassLoader
import me.hwiggy.extensible.contract.Extension
import java.io.File

abstract class JarExtension<D : JarDescriptor> : Extension {
    /**
     * The [JarClassLoader] responsible for loading this [Extension]'s classes.
     */
    lateinit var classLoader: JarClassLoader
        internal set

    /**
     * The [JarDescriptor] responsible for holding information about this [Extension]
     */
    lateinit var descriptor: D
        internal set

    /**
     * A lazily populated reference to the Jar's file on disk.
     */
    lateinit var lazySource: File
        internal set

    /**
     * Evaluates the lazySource to return the true source
     * Caches the result.
     */
    final override val sourceFile by lazy { lazySource }

    val name by lazy(descriptor::name)
}