package io.smallibs.aktor.engine

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorExecution
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Envelop
import io.smallibs.aktor.core.ActorImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.core.ActorUniverse

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
