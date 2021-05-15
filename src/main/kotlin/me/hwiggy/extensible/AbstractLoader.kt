package me.hwiggy.extensible

import me.hwiggy.extensible.contract.Descriptor
import me.hwiggy.extensible.contract.Extension
import me.hwiggy.extensible.contract.ExtensionLoader
import me.hwiggy.extensible.contract.LoadStrategy
import me.hwiggy.extensible.exception.AmbiguousExtensionException
import me.hwiggy.extensible.exception.CompositeException
import me.hwiggy.extensible.exception.InvalidExtensionException
import me.hwiggy.extensible.exception.UnknownDependencyException
import java.io.File
import java.io.FileFilter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A basic implementation of the [ExtensionLoader] that bootstraps the [Extension] loading functions.
 *
 * @param[strategy] The [LoadStrategy] that should be used for this loader.
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
abstract class AbstractLoader<D : Descriptor, E : Extension>(
    override val strategy: LoadStrategy<D, E>
) : ExtensionLoader<D, E> {
    /**
     * An index of the loaded [Extension]s known by this [ExtensionLoader]
     * Marked as public to expose the collection to implementation
     */
    val extensionIndex = HashMap<String, E>()
    override fun loadExtensions(folder: File, filter: FileFilter): List<E> {
        val loadOrder = ArrayList<File>()
        val fileIndex = HashMap<String, File>()
        val hardDependencies = HashMap<String, MutableSet<String>>()
        val softDependencies = HashMap<String, MutableSet<String>>()
        folder.listFiles(filter)?.forEach {
            try {
                val descriptor = strategy.readDescriptor(it)
                fileIndex.compute(descriptor.name) { name, indexed ->
                    if (indexed != null) throw AmbiguousExtensionException(name, indexed, it)
                    if (!permitExtension(name)) throw InvalidExtensionException(
                        "Extension name '$name' not permitted! (${it.path})"
                    ) else it
                }
                hardDependencies[descriptor.name] = descriptor.hardDependencies.toMutableSet()
                softDependencies[descriptor.name] = descriptor.softDependencies.toMutableSet()
            } catch (ex: Exception) { handleUncaught(ex) }
        }
        do {
            // Find extensions with no hard dependencies, preferring ones with no soft dependencies
            // Make sure to exclude those that are already in the load order
            val next = fileIndex.filterValues { it !in loadOrder }.keys.filter {
                hardDependencies[it]?.isEmpty() ?: true
            }.sortedBy { softDependencies[it]?.size ?: 0 }
            // Build the extension load order
            loadOrder.addAll(next.map { fileIndex[it]!! })
            // Filter the 'loaded' extension from dependency indices
            next.forEach {
                hardDependencies.remove(it)
                softDependencies.remove(it)
            }
            // Filter the 'loaded' extension from dependency sets
            hardDependencies.values.forEach { it.removeAll(next) }
            softDependencies.values.forEach { it.removeAll(next) }
        } while (next.isNotEmpty())
        hardDependencies.forEach { (extension, unresolved) ->
            handleUncaught(CompositeException(
                "Could not load extension '$extension'!",
                UnknownDependencyException(unresolved)
            ))
        }
        return loadOrder.mapNotNull {
            try { strategy.loadExtension(it) }
            catch (ex: Exception) {
                handleUncaught(ex); null
            }
        }
    }

    override fun loadExtension(file: File): E = try {
        val descriptor = strategy.readDescriptor(file)
        val name = descriptor.name
        if (!permitExtension(name)) throw InvalidExtensionException(
            "Extension name '$name' not permitted! (${file.path})"
        )
        extensionIndex[name]?.let { indexed ->
            throw AmbiguousExtensionException(
                name, indexed.getSourceFile(), file
            )
        }
        descriptor.hardDependencies.toMutableSet()
            .apply { removeIf(extensionIndex::containsKey) }
            .let { if (it.isNotEmpty()) throw UnknownDependencyException(it) }
        strategy.loadExtension(file).also(Extension::load).also {
            extensionIndex[name] = it
        }
    } catch (err: Throwable) {
        throw CompositeException("Could not load extension ${file.path}", err)
    }
}