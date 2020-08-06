package io.smallibs.aktor.core

import io.smallibs.aktor.Envelop
import kotlin.jvm.Synchronized

// Investigate: Can we use coroutine in this section instead of synchronized block
internal class ActorMailbox<T> {

    private val envelops: ArrayList<Envelop<T>> = arrayListOf()

    @Synchronized
    fun deliver(envelop: Envelop<T>) {
        envelops.add(envelop)
    }

    @Synchronized
    fun next(): Envelop<T>? =
        if (envelops.isEmpty()) {
            null
        } else {
            envelops.removeAt(0)
        }
}
