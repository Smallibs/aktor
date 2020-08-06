package io.smallibs.aktor.core

import io.smallibs.aktor.Envelop

// Investigate: Can we use coroutine in this section?
internal class ActorMailbox<T> {

    private var envelops: ArrayList<Envelop<T>> = arrayListOf()

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
