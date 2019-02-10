package io.smallibs.aktor

import io.smallibs.aktor.core.System

interface ActorReference<T> {

    val address: ActorAddress

    infix fun tell(content: System.Protocol) {
        tell(SystemEnvelop(content))
    }

    infix fun tell(content: T) {
        tell(ProtocolEnvelop(content))
    }

    infix fun tell(envelop: Envelop<T>)

}
