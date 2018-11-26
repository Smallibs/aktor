package org.smalllibs.actor.engine

import org.smalllibs.actor.*
import org.smalllibs.actor.impl.ActorImpl
import org.smalllibs.actor.reference.ActorReferenceImpl
import java.util.*

class ActorDispatcher(private val execution: ActorExecution) {

    private val actors: MutableMap<ActorAddress<*>, ActorImpl<*>> = Collections.synchronizedMap(HashMap())

    fun <T> register(reference: ActorReferenceImpl<T>, receive: (Actor<T>, Envelop<T>) -> Unit): Actor<T> =
        register(reference, Behavior(receive))

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): Actor<T> {

        val actor = ActorImpl(reference)
        actor.start(behavior)

        actors[reference.address] = actor // TODO(didier) check when an address is already used.
        execution.manage(actor)

        return actor
    }

    fun <T> deliver(address: ActorAddress<T>, envelop: Envelop<T>) =
        actor(address)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyActorTurn(actor)
        }


    private fun <T> actor(address: ActorAddress<T>): ActorImpl<T>? =
        actors[address]?.let {
            if (it.self().address == address) {
                @Suppress("UNCHECKED_CAST")
                it as ActorImpl<T> // Ugly cast to be removed!
            } else {
                null
            }
        }

}
