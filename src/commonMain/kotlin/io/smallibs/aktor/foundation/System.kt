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
    data class ToDeadLetter(val message: DeadLetter.Protocol) : Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private val core: CoreReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Live -> {
                    val directory = actor.actorFor(Directory.new(), Directory.name)
                    val deadLetter = actor.actorFor(DeadLetter.new(), DeadLetter.name)

                    actor become protocol(directory, deadLetter)

                    Directory from actor.context.self register deadLetter
                }
                is Core.Killed ->
                    actor.context.self tell ToDirectory(Directory.UnregisterActor(message.content.reference))
                else ->
                    Behaviors.core(actor, message)
            }.exhaustive
        }

    private fun protocol(
        directory: ActorReference<Directory.Protocol>,
        deadLetter: ActorReference<DeadLetter.Protocol>
    ): ProtocolReceiver<Protocol> =
        { actor, message ->
            when (message.content) {
                is ToDirectory ->
                    directory tell message.content.message
                is ToDeadLetter->
                    deadLetter tell message.content.message
                is Install -> {
                    actor actorFor message.content.behavior
                    Unit
                }
                else ->
                    reject
            }.exhaustive
        }

    fun new(): Behavior<System.Protocol> = Core.Behaviors.stashed(System.core)

}