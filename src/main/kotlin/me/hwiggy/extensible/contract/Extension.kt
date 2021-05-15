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
    fun load()

    /**
     * Signals to the implementation that the enable lifecycle is being run.
     */
    fun enable()

    /**
     * Signals to the implementation that the disable lifecycle is being run.
     */
    fun disable()

    /**
     * @return The source [File] for this Extension
     */
    fun getSourceFile(): File
}