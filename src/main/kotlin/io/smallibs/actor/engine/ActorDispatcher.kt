package io.smallibs.actor.engine

import io.smallibs.actor.Actor
import io.smallibs.actor.ActorExecution
import io.smallibs.actor.ActorReference
import io.smallibs.actor.Behavior
import io.smallibs.actor.Envelop
import io.smallibs.actor.core.ActorImpl
import io.smallibs.actor.core.ActorReferenceImpl
import io.smallibs.actor.core.ActorUniverse

class ActorDispatcher private constructor(private val universe: ActorUniverse, private val execution: ActorExecution) {

    constructor(runner: ActorRunner) : this(
        ActorUniverse(),
        ActorExecutionImpl(runner)
    )

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
