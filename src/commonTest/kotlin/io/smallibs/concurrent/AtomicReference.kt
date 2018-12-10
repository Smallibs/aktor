package io.smallibs.concurrent

import kotlin.jvm.Synchronized

class AtomicReference<A>(var value: A) {

    @Synchronized
    fun set(a: A) {
        value = a
    }

    @Synchronized
    fun get(): A {
        return value
    }

}

@Synchronized
fun AtomicReference<Int>.addAndGet(content: Int): Int {
    val current = value
    value += content
    return current
}

fun AtomicReference<Int>.incrementAndGet(): Int {
    return addAndGet(1)
}
