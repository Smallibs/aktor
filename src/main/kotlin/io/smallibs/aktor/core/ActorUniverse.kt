package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorReference
import java.util.*

class ActorUniverse {

    private val actors: MutableMap<ActorAddress, ActorImpl<*>> = Collections.synchronizedMap(HashMap())

    fun parent(reference: ActorReference<*>): ActorReference<*>? =
        actors.values.singleOrNull { it.context.self.address.parentOf(reference.address) }?.let { it.context.self }

    fun children(reference: ActorReference<*>): Collection<ActorReference<*>> =
        actors.values.filter { reference.address.parentOf(it.context.self.address) }.map { it.context.self }

    fun <T> add(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors.put(reference.address, actor)
    }

    fun <T> remove(reference: ActorReferenceImpl<T>) {
        actors.remove(reference.address)
    }

    fun <T> find(reference: ActorReference<T>): ActorImpl<T>? =
        actors.get(reference.address)?.let {
            if (it.context.self.address == reference.address) {
                @Suppress("UNCHECKED_CAST")
                it as ActorImpl<T> // Ugly cast to be removed!
            } else {
                null
            }
        }


}