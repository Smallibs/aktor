package io.smallibs.aktor.foundation

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Envelop
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object DeadLetter {

    const val name = "dead-letter"

    interface Protocol
    data class NotManaged(val reference: ActorReference<*>, val envelop: Envelop<*>, val reason: String) : Protocol
    data class Configure(val notifier: Notifier) : Protocol

    private fun registry(notifier: Notifier): ProtocolBehavior<DeadLetter.Protocol> =
        { actor, message ->
            when (message.content) {
                is NotManaged -> {
                    notifier.notify(message.content.reference, message.content.envelop)
                    actor.same()
                }
                is Configure ->
                    actor become registry(message.content.notifier)
                else ->
                    reject
            }.exhaustive.value
        }

    fun new(notifier: Notifier = Notifier.default()) =
        registry(notifier)

    interface Notifier {

        fun notify(reference: ActorReference<*>, envelop: Envelop<*>)

        class Delegate(val notifier: (ActorReference<*>, Envelop<*>) -> Unit) : Notifier {
            override fun notify(reference: ActorReference<*>, envelop: Envelop<*>) = notifier(reference, envelop)
        }

        companion object {
            fun default() = Delegate { reference, message ->
                println("[Warning] ${reference.address} cannot manage ${message}")
            }
        }
    }

    infix fun from(actor: Actor<*>): Bridge =
        DeadLetter from actor.context.self

    infix fun from(reference: ActorReference<*>): Bridge = Bridge { message ->
        reference tell Core.ToRoot(System.ToDeadLetter(message))
    }

    class Bridge(val bridge: (Protocol) -> Unit) {

        infix fun configure(notifier: (ActorReference<*>, Envelop<*>) -> Unit) =
            bridge(Configure(DeadLetter.Notifier.Delegate(notifier)))

    }

}

