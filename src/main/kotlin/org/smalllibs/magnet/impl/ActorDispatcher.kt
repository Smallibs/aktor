package org.smalllibs.magnet.impl

import org.smalllibs.magnet.Actor
import org.smalllibs.magnet.ActorAddress
import org.smalllibs.magnet.Behavior
import org.smalllibs.magnet.Envelop

internal class ActorDispatcher {

    private val actors: MutableMap<ActorAddress<*>, ActorImpl<*>> = HashMap()
    private val execution: ActorExecution = ActorExecution()

    fun <T> register(reference: ActorReferenceImpl<T>, receive: (Actor<T>, Envelop<T>) -> Unit): Actor<T> =
            register(reference, Behavior(receive))

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): Actor<T> {
        val actor = ActorImpl(reference)
        actor.start(behavior)

        // TODO(didier) check when an address is already used.
        actors[reference.address()] = actor
        execution.manage(actor)

        return actor
    }

    fun <T> deliver(address: ActorAddress<T>, envelop: Envelop<T>) {
        actor(address)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyActorTurn(actor)
        }
    }

    private fun <T> actor(address: ActorAddress<T>): ActorImpl<T>? {
        val actor = actors[address]

        return actor?.let {
            if (actor.self().address() == address) {
                @Suppress("UNCHECKED_CAST")
                actor as ActorImpl<T>
            } else {
                null
            }
        }
    }

}
