package org.smalllibs.actor

import org.smalllibs.actor.core.ActorImpl

interface ActorExecution {

    fun manage(actor: ActorImpl<*>)

    fun notifyEpoch(address: ActorAddress)

}