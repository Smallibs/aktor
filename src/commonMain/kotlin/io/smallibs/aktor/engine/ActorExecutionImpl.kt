package io.smallibs.aktor.engine

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorExecution
import io.smallibs.aktor.ActorRunner
import io.smallibs.aktor.core.ActorImpl
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.jvm.Synchronized

internal class ActorExecutionImpl(private val runner: ActorRunner) : ActorExecution {

    internal enum class Status { STOPPED, RUN }

    private val actors: MutableMap<ActorAddress, Pair<ActorImpl<*>, AtomicRef<Status>>> = HashMap()

    @Synchronized
    override fun manage(actor: ActorImpl<*>) {
        actors[actor.context.self.address] = Pair(actor, atomic(Status.STOPPED))
    }

    @Synchronized
    override fun notifyEpoch(address: ActorAddress) {
        actors[address]?.let { performEpoch(it.first, it.second) }
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
