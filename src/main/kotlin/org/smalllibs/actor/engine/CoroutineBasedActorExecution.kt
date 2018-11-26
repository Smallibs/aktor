@file:Suppress("JoinDeclarationAndAssignment")

package org.smalllibs.actor.impl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.smalllibs.actor.ActorExecution
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

internal class CoroutineBasedActorExecution(nbThread: Int = 0) : ActorExecution {

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

    private fun availableProcessors(nbThread: Int): Int {
        return Math.min(Runtime.getRuntime().availableProcessors(), Math.max(4, nbThread))
    }

}
