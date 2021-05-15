package me.hwiggy.extensible.exception

import java.lang.RuntimeException

/**
 * An Exception that is thrown when a hard dependency is un-met after resolution of the load-order.
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
class UnknownDependencyException(
    unresolved: Set<String>
) : RuntimeException("Unresolved dependencies: ${unresolved.joinToString()}")