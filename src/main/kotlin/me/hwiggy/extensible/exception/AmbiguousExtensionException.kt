package me.hwiggy.extensible.exception

import java.io.File
import java.lang.RuntimeException

/**
 * This Exception is thrown when an Extension is already indexed by the ExtensionLoader.
 * @param[name] The shared name of the duplicate Extension
 * @param[indexed] The [File] source of the already-indexed Extension
 * @param[duplicate] The [File] source of the duplicating Extension
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
class AmbiguousExtensionException(
    name: String, indexed: File, duplicate: File
) : RuntimeException("Extension '$name' from ${indexed.path} is duplicated by ${duplicate.path}!")