package io.smallibs.aktor.engine

import io.smallibs.aktor.*
import io.smallibs.aktor.core.ActorImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.core.ActorUniverse

class ActorDispatcher private constructor(private val universe: ActorUniverse, private val execution: ActorExecution) {

    constructor(runner: ActorRunner) : this(
        ActorUniverse(),
        ActorExecutionImpl(runner)
    )

    fun <T> register(reference: ActorReferenceImpl<T>, receive: ProtocolReceiver<T>): ActorImpl<T> =
        register(reference, Behavior of receive)

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): ActorImpl<T> =
        ActorImpl(reference, behavior)
            .also { actor ->
                universe.add(reference, actor)
                execution.manage(actor)
            }

    fun <T> deliver(reference: ActorReference<T>, envelop: Envelop<T>) =
        universe.find(reference)?.let { actor ->
            actor.deliver(envelop)
            execution.notifyEpoch(actor.context.self.address)
        }

    fun <T> parent(reference: ActorReferenceImpl<T>): ActorReference<*>? =
        universe.parent(reference)

    fun <T> children(reference: ActorReferenceImpl<T>): Collection<ActorReference<*>> =
        universe.children(reference)

}
