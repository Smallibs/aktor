package io.smallibs.aktor

import io.smallibs.aktor.core.ActorImpl

interface ActorExecution {

    fun manage(actor: ActorImpl<*>)

    fun notifyEpoch(address: ActorAddress)
}
