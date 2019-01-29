package io.smallibs.aktor

import io.smallibs.aktor.system.SystemMessage

interface ActorReference<T> {

    val address: ActorAddress

    infix fun tell(envelop: Envelop<T>)

    infix fun tell(content: T) {
        tell(ProtocolEnvelop(content))
    }

    infix fun tell(content: SystemMessage) {
        tell(SystemEnvelop(content))
    }

}
