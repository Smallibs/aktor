package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorReference
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.atomicfu.updateAndGet

class ActorUniverse {

    private val actors: AtomicRef<Map<ActorAddress, ActorImpl<*>>> = atomic(mapOf())

    fun root(): ActorReference<*>? {
        var response: ActorReference<*>? = null

        actors.update {
            if (it.isNotEmpty()) {
                response = root(it.entries.first().value.context.self)
            }

            it
        }

        return response
    }

    tailrec fun root(reference: ActorReference<*>): ActorReference<*> {
        return when (val parent = parent(reference)) {
            null -> reference
            else -> root(parent)
        }
    }

    fun parent(reference: ActorReference<*>): ActorReference<*>? {
        var response: ActorReference<*>? = null

        actors.update {
            response = it.values
                .singleOrNull { it.context.self.address.parentOf(reference.address) }
                ?.context
                ?.self

            it
        }

        return response
    }

    fun children(reference: ActorReference<*>): Collection<ActorReference<*>> {
        var response: Collection<ActorReference<*>> = listOf()

        actors.update { it ->
            response = it.values
                .filter { reference.address.parentOf(it.context.self.address) }
                .map { it.context.self }

            it
        }

        return response
    }

    fun <T> add(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors.update {
            it + (reference.address to actor)
        }
    }

    fun <T> remove(reference: ActorReferenceImpl<T>): Boolean {
        val size = actors.value.size

        val newSize = actors.updateAndGet { it ->
            it.filter { it.key != reference.address }
        }.size

        return size != newSize
    }

    fun <T> find(reference: ActorReference<T>): ActorImpl<T>? {
        var response: ActorImpl<T>? = null

        actors.update { it ->
            response = it[reference.address]?.let {
                if (it.context.self.address == reference.address) {
                    @Suppress("UNCHECKED_CAST")
                    it as ActorImpl<T> // Ugly cast to be removed!
                } else {
                    null
                }
            }

            it
        }

        return response
    }
}
