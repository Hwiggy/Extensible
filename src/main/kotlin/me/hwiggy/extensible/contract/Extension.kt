package me.hwiggy.extensible.contract

import java.io.File

/**
 * This interface is a marker interface for Extension entry-points.
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
interface Extension {
    /**
     * Signals to the implementation that the load lifecycle is being run.
     */
    fun load() = Unit

    /**
     * Signals to the implementation that the enable lifecycle is being run.
     */
    fun enable() = Unit

    /**
     * Signals to the implementation that the disable lifecycle is being run.
     */
    fun disable() = Unit

    /**
     * The source [File] for this Extension
     */
    val sourceFile: File
}