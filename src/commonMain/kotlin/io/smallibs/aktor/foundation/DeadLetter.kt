package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Envelop
import io.smallibs.aktor.ProtocolReceiver

object DeadLetter {

    const val name = "dead-letter"

    interface Protocol
    data class NotManaged(val reference: ActorReference<*>, val envelop: Envelop<*>) : Protocol

    private fun registry(logger: (ActorReference<*>, Envelop<*>) -> Unit): ProtocolReceiver<DeadLetter.Protocol> =
        { _, message ->
            when (message.content) {
                is NotManaged ->
                    logger(message.content.reference, message.content.envelop)
            }
        }

    fun new(logger: (ActorReference<*>, Envelop<*>) -> Unit = { r, m -> println("actor $r executes $m") }) =
        registry(logger)

}