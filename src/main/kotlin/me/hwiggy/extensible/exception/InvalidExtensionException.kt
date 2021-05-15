package me.hwiggy.extensible.exception

import java.lang.RuntimeException

/**
 * An Exception that is thrown when an Extension is invalid for any reason.
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
class InvalidExtensionException(cause: String): RuntimeException(cause)