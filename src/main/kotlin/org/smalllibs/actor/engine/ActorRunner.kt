package org.smalllibs.actor.engine

interface ActorRunner {

    fun execute(run: () -> Unit)


}
