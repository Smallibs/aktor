package io.smallibs.aktor.foundation

import io.smallibs.aktor.Behavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object User {

    const val name = "user"

    interface Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private fun registry(): ProtocolBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is Install ->
                    actor actorFor message.content.behavior
                else ->
                    reject
            }.exhaustive

            actor.behavior()
        }

    fun new() = Behavior of User.registry()

}