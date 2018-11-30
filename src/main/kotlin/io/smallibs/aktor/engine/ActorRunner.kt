package io.smallibs.aktor.engine

interface ActorRunner {

    fun execute(run: () -> Unit)

    companion object {
        fun coroutine() = CoroutineBasedRunner()
        fun threaded(nbThread: Int? = null) = ThreadBasedRunner(nbThread)
    }

}
