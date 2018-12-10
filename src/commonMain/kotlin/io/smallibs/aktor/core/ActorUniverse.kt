package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorReference
import kotlin.jvm.Synchronized

class ActorUniverse {

    private val actors: MutableMap<ActorAddress, ActorImpl<*>> = mutableMapOf()

    @Synchronized
    fun parent(reference: ActorReference<*>): ActorReference<*>? =
        actors.values
            .singleOrNull { it.context.self.address.parentOf(reference.address) }
            ?.context
            ?.self

    @Synchronized
    fun children(reference: ActorReference<*>): Collection<ActorReference<*>> =
        actors.values
            .filter { reference.address.parentOf(it.context.self.address) }
            .map { it.context.self }

    @Synchronized
    fun <T> add(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors[reference.address] = actor
    }

    @Synchronized
    fun <T> remove(reference: ActorReferenceImpl<T>) {
        actors.remove(reference.address)
    }

    @Synchronized
    fun <T> find(reference: ActorReference<T>): ActorImpl<T>? =
        actors[reference.address]?.let {
            if (it.context.self.address == reference.address) {
                @Suppress("UNCHECKED_CAST")
                it as ActorImpl<T> // Ugly cast to be removed!
            } else {
                null
            }
        }


}