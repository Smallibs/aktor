package org.smalllibs.actor.engine

import org.smalllibs.actor.ActorExecution
import org.smalllibs.actor.impl.ActorImpl
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

internal class ActorExecutionImpl(private val runner: ActorRunner) : ActorExecution {

    internal enum class Status {
        STOPPED, RUN
    }

    private val actors: MutableMap<ActorImpl<*>, AtomicReference<Status>> = HashMap()

    private val schedulingService: ExecutorService = Executors.newSingleThreadExecutor()

    override fun manage(actor: ActorImpl<*>) =
        this.schedulingService.execute {
            actors[actor] = AtomicReference(Status.STOPPED)
        }

    override fun notifyActorTurn(actor: ActorImpl<*>) =
        this.schedulingService.execute {
            actors[actor]?.let { performActorTurn(actor, it) }
        }

    //
    // Private behaviors
    //

    private fun performActorTurn(actor: ActorImpl<*>, status: AtomicReference<Status>) {
        if (status.get() == Status.STOPPED) {
            actor.nextTurn()?.let { action ->
                status.set(Status.RUN)

                runner.execute {
                    action()
                    status.set(Status.STOPPED)
                    notifyActorTurn(actor)
                }
            }
        }
    }

}
