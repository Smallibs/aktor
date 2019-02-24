package io.smallibs.aktor

import io.smallibs.aktor.core.Core

interface ActorReference<T> {

    val address: ActorAddress

    infix fun tell(content: Core.Protocol) {
        tell(CoreEnvelop(content))
    }

    infix fun tell(content: T) {
        tell(ProtocolEnvelop(content))
    }

    infix fun tell(envelop: Envelop<T>)

}
