package io.smallibs.actor

import io.smallibs.actor.core.ActorImpl

interface ActorExecution {

    fun manage(actor: ActorImpl<*>)

    fun notifyEpoch(address: ActorAddress)

}