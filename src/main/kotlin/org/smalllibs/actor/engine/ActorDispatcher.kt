package org.smalllibs.actor.engine

import org.smalllibs.actor.*
import org.smalllibs.actor.core.ActorImpl
import org.smalllibs.actor.core.ActorReferenceImpl
import org.smalllibs.actor.core.ActorUniverse

class ActorDispatcher(private val universe: ActorUniverse, private val execution: ActorExecution) {

    fun <T> register(reference: ActorReferenceImpl<T>, receive: (Actor<T>, Envelop<T>) -> Unit): ActorImpl<T> =
        register(reference, Behavior(receive))

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): ActorImpl<T> {
        val actor = ActorImpl(reference, behavior)

        universe.add(reference, actor)
        execution.manage(actor)

        return actor
    }

    fun <T> deliver(reference: ActorReference<T>, envelop: Envelop<T>) =
        universe.find(reference)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyEpoch(actor.context.self.address)
        }

    fun <T> parent(reference: ActorReferenceImpl<T>): ActorReference<*>? = universe.parent(reference)

    fun <T> children(reference: ActorReferenceImpl<T>): Collection<ActorReference<*>> = universe.children(reference)

}
