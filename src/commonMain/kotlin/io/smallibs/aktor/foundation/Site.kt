package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Receiver

class Site {

    interface Protocol
    object Start : Protocol
    data class UserInstall<R>(val behavior: Behavior<R>) : Protocol
    data class SystemInstall<R>(val behavior: Behavior<R>) : Protocol

    data class Runtime(val system: ActorReference<System.Protocol>, val user: ActorReference<User.Protocol>)

    private fun registry(): Receiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Start -> {
                    val system = actor actorFor System.new()
                    val user = actor actorFor User.new()

                    system tell System.Start

                    actor become registry(Runtime(system, user))
                }
            }
        }

    private fun registry(runtime: Runtime): Receiver<Protocol> =
        { _, message ->
            when (message.content) {
                is SystemInstall<*> -> runtime.system tell System.Install(message.content.behavior)
                is UserInstall<*> -> runtime.user tell User.Install(message.content.behavior)
            }
        }

    companion object {
        fun new() = Behavior of Site().registry()
    }

}