package me.hwiggy.extensible.exception

import java.io.File
import java.lang.Exception
import java.lang.RuntimeException

/**
 * An Exception that is thrown when a Descriptor is malformed or absent from a source [File]
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
class InvalidDescriptionException(
    from: File, source: Exception? = null
) : RuntimeException("Invalid description for file ${from.path}:", source)