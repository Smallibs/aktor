package org.smalllibs.magnet.impl

import org.smalllibs.magnet.Envelop

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
