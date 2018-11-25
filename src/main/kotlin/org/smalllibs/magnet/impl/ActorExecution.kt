@file:Suppress("JoinDeclarationAndAssignment")

package org.smalllibs.magnet.impl

import org.smalllibs.magnet.impl.ActorExecution.Status.RUN
import org.smalllibs.magnet.impl.ActorExecution.Status.STOPPED
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

internal class ActorExecution(nbThread: Int = 0) {

    internal enum class Status {
        STOPPED, RUN
    }

    private val actors: MutableMap<ActorImpl<*>, AtomicReference<Status>>

    private val schedulingService: ExecutorService
    private val actorService: ExecutorService

    init {
        this.schedulingService = Executors.newSingleThreadExecutor()
        this.actorService = Executors.newFixedThreadPool(availableProcessors(nbThread))
        this.actors = HashMap()
    }

    fun manage(actor: ActorImpl<*>) =
            this.schedulingService.execute {
                actors[actor] = AtomicReference(STOPPED)
            }

    fun notifyActorTurn(actor: ActorImpl<*>) =
            this.schedulingService.execute {
                actors[actor]?.let { performActorTurn(actor, it) }
            }

    //
    // Private behaviors
    //

    private fun performActorTurn(actor: ActorImpl<*>, status: AtomicReference<Status>) {
        if (status.get() == STOPPED) {
            actor.nextTurn()?.let { action ->
                status.set(RUN)

                actorService.execute {
                    action()
                    status.set(STOPPED)
                    notifyActorTurn(actor)
                }
            }
        }
    }

    private fun availableProcessors(nbThread: Int): Int {
        return Math.min(Runtime.getRuntime().availableProcessors(), Math.max(4, nbThread))
    }

}
