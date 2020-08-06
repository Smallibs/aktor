package io.smallibs.aktor.runner

import io.smallibs.aktor.ActorRunner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadBasedRunner(nbThread: Int? = null) : ActorRunner {

    private val availableProcessors: Int = (nbThread ?: Runtime.getRuntime().availableProcessors()).coerceAtLeast(2)

    private val actorService: ExecutorService = Executors.newFixedThreadPool(availableProcessors)

    override fun execute(run: () -> Unit) = this.actorService.execute(run)

}
