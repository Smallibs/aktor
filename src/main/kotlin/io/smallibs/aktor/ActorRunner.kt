package io.smallibs.aktor

import io.smallibs.aktor.runner.CoroutineBasedRunner
import io.smallibs.aktor.runner.ThreadBasedRunner

interface ActorRunner {

    fun execute(run: () -> Unit)

    companion object {
        fun coroutine() = CoroutineBasedRunner()
        fun threaded(nbThread: Int? = null) = ThreadBasedRunner(nbThread)
    }

}
