package io.smallibs.aktor.engine

interface ActorRunner {

    fun execute(run: () -> Unit)


}
