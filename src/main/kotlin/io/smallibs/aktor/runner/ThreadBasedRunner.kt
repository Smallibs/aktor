package io.smallibs.aktor.runner

import io.smallibs.aktor.ActorRunner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadBasedRunner(nbThread: Int? = null) : ActorRunner {

    private val actorService: ExecutorService = Executors.newFixedThreadPool(availableProcessors(nbThread))

    override fun execute(run: () -> Unit) {
        this.actorService.execute(run)
    }

    private fun availableProcessors(nbThread: Int?): Int =
        (nbThread ?: Runtime.getRuntime().availableProcessors())
            .coerceAtLeast(2)

}
