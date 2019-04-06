package io.smallibs.aktor.utils

val reject = null

class NotExhaustive : Exception()

class Exhaustive<T>(val value: T)

val <T> T?.exhaustive: Exhaustive<T>
    get() =
        when (this) {
            null -> throw NotExhaustive()
            else -> Exhaustive(this)
        }
