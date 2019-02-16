package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Receiver

class System {

    interface Protocol
    object Start : Protocol
    data class ToDirectory(val message: Directory.Protocol) : Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private fun registry(): Receiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Start -> {
                    val directory = actor actorFor Directory.new()
                    actor become registry(directory)
                }
            }

        }

    private fun registry(directory: ActorReference<Directory.Protocol>): Receiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is ToDirectory -> directory tell message.content.message
                is Install -> actor actorFor message.content.behavior
            }
        }

    companion object {
        fun new() = Behavior of System().registry()
    }

}