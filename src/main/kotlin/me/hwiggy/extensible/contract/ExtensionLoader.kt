package me.hwiggy.extensible.contract

import java.io.Closeable
import java.io.File
import java.io.FileFilter

/**
 * The [ExtensionLoader] is responsible for loading [Extension]s from a specified file or folder.
 * @author Hunter N. Wignall
 * @version May 15, 2021
 *
 * @param[D] Type constraint for the [Descriptor] to be read by the [LoadStrategy]
 * @param[E] Type constraint for the [Extension] to be loaded by this [ExtensionLoader]
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
interface ExtensionLoader<D : Descriptor, E : Extension> : Closeable {
    /**
     * The [LoadStrategy] that will provide a [Descriptor] and eventually an [Extension] from a file or folder.
     */
    val strategy: LoadStrategy<D, E>

    /**
     * An index of the loaded [Extension]s known by this [ExtensionLoader]
     */
    val extensionIndex: MutableMap<String, E>

    /**
     * Attempts to load [Extension]s from the specified folder.
     * A [FileFilter] may be used to filter the types of [File] to attempt a load from.
     *
     * @param[folder] The [File] folder to try loading [Extension]s from.
     * @param[filter] The [FileFilter] to be used when determining what [File]s to load from.
     *
     * @return A [List] of [E] loaded extensions
     */
    fun loadExtensions(folder: File, filter: FileFilter = FileFilter { true }): List<E>

    /**
     * Attempts to load an [Extension] from the given [File]
     *
     * @param[file] The [File] to try loading from.
     *
     * @return The loaded [E] extension.
     */
    fun loadExtension(file: File): E

    /**
     * Injects an [Extension] into the [extensionIndex] with a specified name.
     * @param[name] The name of the [Extension] to index.
     * @param[extension] The [Extension] to be indexed.
     */
    fun indexExtension(name: String, extension: E)

    /**
     * Validates the name of an [Extension] before it is loaded.
     * @param[file] The file defining the [Extension] being loaded.
     * @param[descriptor] The [Descriptor] stored inside this [Extension], for convenience.
     * @return Whether or not this [Extension] should be permitted.
     */
    fun permitExtension(file: File, descriptor: D): Boolean

    /**
     * Handles uncaught [Throwable] errors from the implementation [ExtensionLoader]
     * @param[ex] The [Throwable] error to handle
     */
    fun handleUncaught(ex: Throwable)

    /**
     * Handles the loading for the provided [Extension]
     * Allows for further customization of the load hook
     */
    fun performLoad(extension: E) = extension.load()

    /**
     * Searches the [extensionIndex] for an [Extension] by its name
     */
    fun findExtension(name: String): E?

    /**
     * Searches the [extensionIndex] for an [Extension] by its [Class]
     */
    fun <T : E> getExtension(type: Class<T>): E?

    /**
     * Gets the values of the [extensionIndex] from this [ExtensionLoader]
     */
    fun getExtensions(): List<E>
}