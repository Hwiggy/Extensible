package me.hwiggy.extensible.binding.jvm.contract

import me.hwiggy.extensible.contract.Descriptor

interface JarDescriptor : Descriptor {
    val mainClass: String
}