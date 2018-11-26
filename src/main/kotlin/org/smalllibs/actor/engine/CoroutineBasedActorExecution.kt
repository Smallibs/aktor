@file:Suppress("JoinDeclarationAndAssignment")

package org.smalllibs.actor.engine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.smalllibs.actor.ActorExecution
import org.smalllibs.actor.impl.ActorImpl
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

internal class CoroutineBasedActorExecution : ActorExecution {

    internal enum class Status {
        STOPPED, RUN
    }

    private val actors: MutableMap<ActorImpl<*>, AtomicReference<Status>>

    private val schedulingService: ExecutorService

    init {
        this.schedulingService = Executors.newSingleThreadExecutor()
        this.actors = HashMap()
    }

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

                GlobalScope.launch {
                    action()
                    status.set(Status.STOPPED)
                    notifyActorTurn(actor)
                }
            }
        }
    }

}
