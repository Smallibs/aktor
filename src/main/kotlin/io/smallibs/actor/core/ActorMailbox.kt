package io.smallibs.actor.core

import io.smallibs.actor.Envelop

// Investigate: Can we use coroutine in this section?
internal class ActorMailbox<T> {

    private var envelops: ArrayList<Envelop<T>> = arrayListOf()

    @Synchronized
    fun deliver(envelop: Envelop<T>) {
        envelops.add(envelop)
    }

    @Synchronized
    fun next(): Envelop<T>? {
        if (envelops.isEmpty()) {
            return null
        } else {
            return envelops.removeAt(0)
        }
    }

}
