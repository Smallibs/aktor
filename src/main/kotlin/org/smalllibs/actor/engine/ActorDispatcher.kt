package org.smalllibs.actor.engine

import org.smalllibs.actor.*
import org.smalllibs.actor.core.ActorImpl
import org.smalllibs.actor.core.ActorReferenceImpl
import java.util.*

class ActorDispatcher(private val execution: ActorExecution) {

    private val actors: MutableMap<ActorAddress, ActorImpl<*>> = Collections.synchronizedMap(HashMap())

    fun <T> register(reference: ActorReferenceImpl<T>, receive: (Actor<T>, Envelop<T>) -> Unit): ActorImpl<T> =
        register(reference, Behavior(receive))

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): ActorImpl<T> {

        val actor = ActorImpl(reference, behavior)

        setActor(reference, actor) // TODO(didier) check when an address is already used.
        execution.manage(actor)

        return actor
    }

    fun <T> deliver(reference: ActorReference<T>, envelop: Envelop<T>) =
        getActor(reference)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyEpoch(actor.context.self().address)
        }

    fun parent(reference: ActorReference<*>): ActorReference<*>? =
        actors.values.singleOrNull { it.context.self().address.parentOf(reference.address) }?.let { it.context.self() }

    fun children(reference: ActorReference<*>): List<ActorReference<*>> =
        actors.values.filter { reference.address.parentOf(it.context.self().address) }.map { it.context.self() }

    //
    // Private behaviors
    //

    private fun <T> setActor(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors[reference.address] = actor
    }

    private fun <T> getActor(reference: ActorReference<T>): ActorImpl<T>? =
        actors[reference.address]?.let {
            if (it.context.self().address == reference.address) {
                @Suppress("UNCHECKED_CAST")
                it as ActorImpl<T> // Ugly cast to be removed!
            } else {
                null
            }
        }

}
