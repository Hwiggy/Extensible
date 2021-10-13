package me.hwiggy.extensible.contract

/**
 * A [Descriptor] holds pertinent information about an Extension such as:
 * `name`: The name of the Extension
 * `version`: The version of the Extension
 * `hardDependencies`: Extensions required to be present before loading
 * `softDependencies`: Extensions optionally present before loading
 *
 * Implementations of this interface may define additional properties
 *
 * @author Hunter N. Wignall
 * @version May 15, 2021
 */
interface Descriptor {
    val name: String
    val version: String
    val hardDependencies: List<String>
    val softDependencies: List<String>
}