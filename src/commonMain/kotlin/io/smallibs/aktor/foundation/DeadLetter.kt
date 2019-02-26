package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Envelop
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object DeadLetter {

    const val name = "dead-letter"

    interface Protocol
    data class NotManaged(val reference: ActorReference<*>, val envelop: Envelop<*>) : Protocol
    data class Configure(val notifier: Notifier) : Protocol

    private fun registry(notifier: Notifier): ProtocolReceiver<DeadLetter.Protocol> =
        { actor, message ->
            when (message.content) {
                is NotManaged ->
                    notifier.notify(message.content.reference, message.content.envelop)
                is Configure ->
                    actor become registry(message.content.notifier)
                else ->
                    reject
            }.exhaustive
        }

    fun new(notifier: Notifier = Notifier.default()) =
        registry(notifier)

    interface Notifier {

        fun notify(reference: ActorReference<*>, envelop: Envelop<*>)

        class Delegate(val notifier: (ActorReference<*>, Envelop<*>) -> Unit) : Notifier {
            override fun notify(reference: ActorReference<*>, envelop: Envelop<*>) = notifier(reference, envelop)
        }

        companion object {
            fun default() = Delegate { _, _ -> Unit }
        }

    }

    infix fun from(system: ActorReference<*>): Bridge = Bridge { message ->
        system tell Core.Escalate(System.ToDeadLetter(message))
    }

    class Bridge(val bridge: (Protocol) -> Unit) {

        infix fun configure(notifier: (ActorReference<*>, Envelop<*>) -> Unit) =
            bridge(Configure(DeadLetter.Notifier.Delegate(notifier)))

    }

}

