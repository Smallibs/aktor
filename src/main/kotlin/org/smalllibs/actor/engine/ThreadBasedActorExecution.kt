package org.smalllibs.actor.engine

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

internal class ThreadBasedActorExecution(nbThread: Int = 0) : AbstractActorExecution() {

    private val actorService: ExecutorService = Executors.newFixedThreadPool(availableProcessors(nbThread))

    override fun execute(run: () -> Unit) {
        this.actorService.execute(run)
    }

    private fun availableProcessors(nbThread: Int): Int {
        return Math.min(Runtime.getRuntime().availableProcessors(), Math.max(2, nbThread))
    }

}
