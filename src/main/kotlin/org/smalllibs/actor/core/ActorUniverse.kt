package org.smalllibs.actor.core

import org.smalllibs.actor.ActorAddress
import org.smalllibs.actor.ActorReference
import java.util.*

class ActorUniverse {

    private val actors: MutableMap<ActorAddress, ActorImpl<*>> = Collections.synchronizedMap(HashMap())

    fun parent(reference: ActorReference<*>): ActorReference<*>? =
        actors.values.singleOrNull { it.context.self.address.parentOf(reference.address) }?.let { it.context.self }

    fun children(reference: ActorReference<*>): Collection<ActorReference<*>> =
        actors.values.filter { reference.address.parentOf(it.context.self.address) }.map { it.context.self }

    fun <T> add(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors[reference.address] = actor
    }

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