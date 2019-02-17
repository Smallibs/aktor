package io.smallibs.aktor.foundation

import io.smallibs.aktor.Behavior
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object User {

    interface Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private fun registry(): ProtocolReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Install -> actor actorFor message.content.behavior
                else -> reject
            }.exhaustive
        }

    fun new() = Behavior of User.registry()

}