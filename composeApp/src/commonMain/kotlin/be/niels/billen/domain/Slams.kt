package be.niels.billen.domain

import kotlin.jvm.JvmInline

@JvmInline
value class Slams(val value: Int) {
    init {
        require(value >= 0) { "slams cannot be negative"}
    }
}