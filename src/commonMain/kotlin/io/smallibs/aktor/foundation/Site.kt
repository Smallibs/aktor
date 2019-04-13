package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.CoreBehavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object Site {

    interface Protocol
    data class UserInstall(val behavior: Behavior<*>) : Protocol
    data class SystemInstall(val behavior: Behavior<*>) : Protocol

    private fun installed(system: ActorReference<System.Protocol>): CoreBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Killed -> {
                    system tell message.content
                }
                is Core.ToRoot ->
                    when (message.content.message) {
                        is System.Protocol ->
                            system tell message.content.message
                        else ->
                            reject
                    }
            }

            actor.same()
        }

    private fun protocol(
        system: ActorReference<System.Protocol>,
        user: ActorReference<User.Protocol>
    ): ProtocolBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is SystemInstall ->
                    system tell System.Install(message.content.behavior)
                is UserInstall ->
                    user tell User.Install(message.content.behavior)
                else ->
                    reject
            }.exhaustive

            actor.same()
        }


    fun new(system: ActorReference<System.Protocol>, user: ActorReference<User.Protocol>): Behavior<Protocol> =
        Behavior of Pair(installed(system), protocol(system, user))
}
