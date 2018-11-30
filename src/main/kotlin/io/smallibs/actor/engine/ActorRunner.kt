package io.smallibs.actor.engine

interface ActorRunner {

    fun execute(run: () -> Unit)


}
