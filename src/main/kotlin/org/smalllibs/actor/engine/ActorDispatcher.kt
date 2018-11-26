package org.smalllibs.actor.engine

import org.smalllibs.actor.*
import org.smalllibs.actor.impl.ActorImpl
import org.smalllibs.actor.reference.ActorReferenceImpl
import java.util.*

class ActorDispatcher(private val execution: ActorExecution) {

    private val actors: MutableMap<ActorAddress, ActorImpl<*>> = Collections.synchronizedMap(HashMap())

    fun <T> register(reference: ActorReferenceImpl<T>, receive: (Actor<T>, Envelop<T>) -> Unit): Actor<T> =
        register(reference, Behavior(receive))

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): Actor<T> {

        val actor = ActorImpl(reference)
        actor.start(behavior)

        setActor(reference, actor) // TODO(didier) check when an address is already used.
        execution.manage(actor)

        return actor
    }

    fun <T> deliver(reference: ActorReference<T>, envelop: Envelop<T>) =
        getActor(reference)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyEpoch(actor.self().address)
        }

    private fun <T> setActor(reference: ActorReferenceImpl<T>, actor: ActorImpl<T>) {
        actors[reference.address] = actor
    }

    private fun <T> getActor(reference: ActorReference<T>): ActorImpl<T>? =
        actors[reference.address]?.let {
            if (it.self().address == reference.address) {
                @Suppress("UNCHECKED_CAST")
                it as ActorImpl<T> // Ugly cast to be removed!
            } else {
                null
            }
        }

}
