package org.smalllibs.actor.engine

import org.smalllibs.actor.ActorAddress
import org.smalllibs.actor.ActorExecution
import org.smalllibs.actor.core.ActorImpl
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

internal class ActorExecutionImpl(private val runner: ActorRunner) : ActorExecution {

    internal enum class Status { STOPPED, RUN }

    private val actors: MutableMap<ActorAddress, Pair<ActorImpl<*>, AtomicReference<Status>>> = HashMap()

    private val schedulingService: ExecutorService = Executors.newSingleThreadExecutor()

    override fun manage(actor: ActorImpl<*>) =
        this.schedulingService.execute {
            actors[actor.context.self.address] = Pair(actor, AtomicReference(Status.STOPPED))
        }

    override fun notifyEpoch(address: ActorAddress) =
        this.schedulingService.execute {
            actors[address]?.let { performEpoch(it.first, it.second) }
        }

    //
    // Private behaviors
    //

    private fun performEpoch(actor: ActorImpl<*>, status: AtomicReference<Status>) {
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
