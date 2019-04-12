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
    data class UserInstall<R>(val behavior: Behavior<R>) : Protocol
    data class SystemInstall<R>(val behavior: Behavior<R>) : Protocol

    private val init: CoreBehavior<Protocol> = { actor, message ->
        when (message.content) {
            is Core.Live -> {
                val system = actor actorFor System.new()
                val user = actor actorFor User.new()

                Behavior of Pair(installed(system), protocol(system, user))
            }
            else ->
                actor.same()
        }
    }

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
                is SystemInstall<*> ->
                    system tell System.Install(message.content.behavior)
                is UserInstall<*> ->
                    user tell User.Install(message.content.behavior)
                else ->
                    reject
            }.exhaustive

            actor.same()
        }


    fun new(): Behavior<Protocol> = Core.Behaviors.stashBehavior(init)
}
