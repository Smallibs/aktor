package org.smalllibs.actor.impl

interface ActorExecution {

    fun manage(actor: ActorImpl<*>)

    fun notifyActorTurn(actor: ActorImpl<*>)

}