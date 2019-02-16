package io.smallibs.aktor.foundation

import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Receiver

class User {

    interface Protocol
    data class Install(val behavior: Behavior<*>) : Protocol


    private fun registry(): Receiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Install -> actor actorFor message.content.behavior
            }
        }

    companion object {
        fun new() = Behavior of User().registry()
    }
}