package me.hwiggy.extensible.exception

import java.lang.RuntimeException

/**
 * Marker class for a Throwable that should be logged with a specific cause.
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
class CompositeException(message: String, cause: Throwable) : RuntimeException(message, cause)