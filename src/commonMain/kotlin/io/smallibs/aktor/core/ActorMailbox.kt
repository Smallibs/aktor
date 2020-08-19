package io.smallibs.aktor.core

import io.smallibs.aktor.Envelop
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class ActorMailbox<T> {

    private val envelops: AtomicRef<List<Envelop<T>>> = atomic(listOf())

    fun deliver(envelop: Envelop<T>) {
        envelops.update {
            it + envelop
        }
    }

    fun next(): Envelop<T>? {
        var response: Envelop<T>? = null

        envelops.update {
            if (it.isNotEmpty()) {
                response = it[0]
                it.drop(1)
            } else {
                it
            }
        }

        return response
    }
}
