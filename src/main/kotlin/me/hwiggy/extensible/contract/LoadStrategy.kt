package me.hwiggy.extensible.contract

import me.hwiggy.extensible.exception.InvalidDescriptionException
import java.io.File
import kotlin.jvm.Throws

/**
 * The [LoadStrategy] defines the following behavior:
 *   How a [Descriptor] should be extracted from a [File]
 *   How an [Extension] should be extracted from a [File]
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
interface LoadStrategy<D : Descriptor, E : Extension> {
    /**
     * Attempts to read a [Descriptor] from the given [File] source
     *
     * @param[source] The source [File] file or folder to read from.
     * @throws[InvalidDescriptionException] In case the [Descriptor] is malformed or not present
     * @return A [D] descriptor describing the [Extension] present at the source
     */
    @Throws(InvalidDescriptionException::class)
    fun readDescriptor(source: File): D

    /**
     * Attempts to load an [Extension] from the given [File] source
     *
     * @param[source] The source [File] file or folder to load from
     * @return The loaded [Extension] present as the source
     */
    fun loadExtension(source: File): E
}