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

    const val name = "system"

    interface Protocol
    data class ToDirectory(val message: Directory.Protocol) : Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private val core: CoreReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Live ->
                    actor become protocol(actor.actorFor(Directory.new(), Directory.name))
                is Core.Killed ->
                    actor.context.self tell ToDirectory(Directory.UnregisterActor(message.content.reference))
                else ->
                    Behaviors.core(actor, message)
            }.exhaustive
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

    fun new(): Behavior<System.Protocol> = Core.Behaviors.stashed(System.core)

}