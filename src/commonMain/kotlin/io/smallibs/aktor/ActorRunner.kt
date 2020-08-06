package io.smallibs.aktor

import io.smallibs.aktor.runner.CoroutineBasedRunner

interface ActorRunner {

    fun execute(run: () -> Unit)

    companion object {
        fun coroutine(): ActorRunner = CoroutineBasedRunner()
    }
}
