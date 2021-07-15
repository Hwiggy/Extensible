package me.hwiggy.extensible.binding

import me.hwiggy.extensible.contract.Descriptor
import me.hwiggy.extensible.contract.Extension
import me.hwiggy.extensible.contract.ExtensionLoader
import me.hwiggy.extensible.exception.AmbiguousExtensionException
import me.hwiggy.extensible.exception.CompositeException
import me.hwiggy.extensible.exception.InvalidExtensionException
import me.hwiggy.extensible.exception.UnknownDependencyException
import java.io.File
import java.io.FileFilter

/**
 * A basic implementation of the [ExtensionLoader] that bootstraps the [Extension] loading functions.
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
abstract class AbstractLoader<D : Descriptor, E : Extension> : ExtensionLoader<D, E> {
    override val extensionIndex = HashMap<String, E>()
    override fun loadExtensions(folder: File, filter: FileFilter): List<E> {
        val loadOrder = ArrayList<File>()
        val fileIndex = HashMap<String, File>()
        val hardDependencies = HashMap<String, MutableSet<String>>()
        val softDependencies = HashMap<String, MutableSet<String>>()
        folder.listFiles(filter)?.forEach { file ->
            try {
                val descriptor = strategy.readDescriptor(file)
                fileIndex.compute(descriptor.name) { name, indexed ->
                    if (indexed != null) throw AmbiguousExtensionException(name, indexed, file)
                    if (!permitExtension(file, descriptor)) throw InvalidExtensionException(
                        "Extension name '$name' not permitted! (${file.path})"
                    ) else file
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
            try { loadExtension(it) }
            catch (ex: Exception) {
                handleUncaught(ex); null
            }
        }
    }

    override fun loadExtension(file: File): E = try {
        val descriptor = strategy.readDescriptor(file)
        val name = descriptor.name
        if (!permitExtension(file, descriptor)) throw InvalidExtensionException(
            "Extension name '$name' not permitted! (${file.path})"
        )
        extensionIndex[name]?.let { indexed ->
            throw AmbiguousExtensionException(
                name, indexed.sourceFile, file
            )
        }
        descriptor.hardDependencies.toMutableSet()
            .apply { removeIf(extensionIndex::containsKey) }
            .let { if (it.isNotEmpty()) throw UnknownDependencyException(it) }
        strategy.loadExtension(file).also(this::performLoad).also { indexExtension(name, it) }
    } catch (err: Throwable) {
        throw CompositeException("Could not load extension ${file.path}", err)
    }

    override fun indexExtension(name: String, extension: E) {
        extensionIndex[name] = extension
    }

    override fun close() { extensionIndex.clear() }
}