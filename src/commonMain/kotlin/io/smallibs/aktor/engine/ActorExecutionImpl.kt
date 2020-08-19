package io.smallibs.aktor.engine

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorExecution
import io.smallibs.aktor.ActorRunner
import io.smallibs.aktor.core.ActorImpl
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class ActorExecutionImpl(private val runner: ActorRunner) : ActorExecution {

    internal enum class Status { STOPPED, RUN }

    private val actors: AtomicRef<Map<ActorAddress, Pair<ActorImpl<*>, AtomicRef<Status>>>> = atomic(mapOf())

    override fun manage(actor: ActorImpl<*>) {
        actors.update {
            it + (actor.context.self.address to (actor to atomic(Status.STOPPED)))
        }
    }

    override fun notifyEpoch(address: ActorAddress) {
        var actor: Pair<ActorImpl<*>, AtomicRef<Status>>? = null

        actors.update {
            actor = it[address]
            it
        }

        actor?.let { performEpoch(it.first, it.second) }
    }

    //
    // Private behaviors
    //

    private fun performEpoch(actor: ActorImpl<*>, status: AtomicRef<Status>) {
        if (status.compareAndSet(Status.STOPPED, Status.RUN)) {
            actor.nextTurn()?.let { action ->
                this.runner.execute {
                    action()
                    status.lazySet(Status.STOPPED)
                    notifyEpoch(actor.context.self.address)
                }
            } ?: status.lazySet(Status.STOPPED)
        }
    }
}
