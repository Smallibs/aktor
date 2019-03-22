package io.smallibs.aktor.engine

import io.smallibs.aktor.*
import io.smallibs.aktor.core.ActorImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.core.ActorUniverse
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.foundation.DeadLetter
import io.smallibs.aktor.foundation.System

class ActorDispatcher(runner: ActorRunner) {

    private val universe: ActorUniverse = ActorUniverse()
    private val execution: ActorExecution = ActorExecutionImpl(runner)

    fun <T> register(reference: ActorReferenceImpl<T>, behavior: Behavior<T>): ActorImpl<T> =
        ActorImpl(reference, behavior)
            .also { actor ->
                universe.add(reference, actor)
                execution.manage(actor)
                reference tell Core.Live
            }

    fun <R> unregister(reference: ActorReferenceImpl<R>): Boolean =
        universe.remove(reference)

    fun <T> deliver(reference: ActorReference<T>, envelop: Envelop<T>): Unit? {
        println("[DEBUG] ${reference.address} <= $envelop ")

        val actor = universe.find(reference)

        return when (actor) {
            null ->
                universe.root()?.let {
                    val message = DeadLetter.NotManaged(reference, envelop, "actor not found")
                    it tell Core.ToRoot(System.ToDeadLetter(message))
                }
            else -> {
                actor.deliver(envelop)
                execution.notifyEpoch(actor.context.self.address)
            }
        }
    }

    fun root(reference: ActorReferenceImpl<*>): ActorReference<*> =
        universe.root(reference)

    fun <T> parent(reference: ActorReferenceImpl<T>): ActorReference<*>? =
        universe.parent(reference)

    fun <T> children(reference: ActorReferenceImpl<T>): Collection<ActorReference<*>> =
        universe.children(reference)

}
