package io.smallibs.aktor.utils

val reject = null

class Exhaustive<T>(val t: T)

val <T> T?.exhaustive: Exhaustive<T>
    get() =
        when (this) {
            null -> throw Exception()
            else -> Exhaustive(this)
        }
