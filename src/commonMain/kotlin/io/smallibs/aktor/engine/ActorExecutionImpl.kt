package io.smallibs.aktor.engine

import io.smallibs.aktor.ActorAddress
import io.smallibs.aktor.ActorExecution
import io.smallibs.aktor.ActorRunner
import io.smallibs.aktor.core.ActorImpl
import kotlin.jvm.Synchronized

internal class ActorExecutionImpl(private val runner: ActorRunner) : ActorExecution {

    internal enum class Status { STOPPED, RUN }

    data class StatusReference(var value: Status) {
        @Synchronized
        fun get() = this.value

        @Synchronized
        fun set(value: Status) {
            this.value = value
        }
    }

    private val actors: MutableMap<ActorAddress, Pair<ActorImpl<*>, StatusReference>> = HashMap()

    @Synchronized
    override fun manage(actor: ActorImpl<*>) {
        actors[actor.context.self.address] = Pair(actor, StatusReference(Status.STOPPED))
    }

    @Synchronized
    override fun notifyEpoch(address: ActorAddress) {
        actors[address]?.let { performEpoch(it.first, it.second) }
    }

    //
    // Private behaviors
    //

    private fun performEpoch(actor: ActorImpl<*>, status: StatusReference) {
        if (status.get() == Status.STOPPED) {
            actor.nextTurn()?.let { action ->
                status.set(Status.RUN)

                this.runner.execute {
                    action()
                    status.set(Status.STOPPED)
                    notifyEpoch(actor.context.self.address)
                }
            }
        }
    }
}
