package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.CoreReceiver
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.core.Core.Behaviors
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object System {

    interface Protocol
    data class ToDirectory(val message: Directory.Protocol) : Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private val core: CoreReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Start ->
                    actor become protocol(actor.actorFor(Directory.new(), Directory.name))
                is Core.Stopped ->
                    actor.context.self tell ToDirectory(Directory.UnregisterActor(message.content.reference))
                else ->
                    Behaviors.core(actor, message)
            }.exhaustive
        }

    private val protocol: ProtocolReceiver<Protocol> = { actor, message ->
        actor.context.self tell message // Stashing
    }

    private fun protocol(directory: ActorReference<Directory.Protocol>): ProtocolReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is ToDirectory ->
                    directory tell message.content.message
                is Install -> {
                    actor actorFor message.content.behavior; Unit
                }
                else ->
                    reject
            }.exhaustive
        }

    fun new(): Behavior<System.Protocol> = Behavior of Pair(System.core, System.protocol)

}