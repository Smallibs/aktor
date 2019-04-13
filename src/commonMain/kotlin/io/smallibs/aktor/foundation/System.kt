package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.CoreBehavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object System {

    const val name = "system"

    interface Protocol
    data class ToDirectory(val message: Directory.Protocol) : Protocol
    data class ToDeadLetter(val message: DeadLetter.Protocol) : Protocol
    data class Install(val behavior: Behavior<*>) : Protocol

    private val init: CoreBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Live -> {
                    val directory = actor.actorFor(Directory.new(), Directory.name)
                    val deadLetter = actor.actorFor(DeadLetter.new(), DeadLetter.name)

                    Directory from actor.context.self register deadLetter

                    Behavior of Pair(running, protocol(directory, deadLetter))
                }
                else ->
                    Core.Behaviors.core<Protocol>()(actor, message)
            }
        }

    private val running: CoreBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is Core.Killed -> {
                    actor.context.self tell ToDirectory(Directory.UnregisterActor(message.content.reference))
                    actor.same()
                }
                is Core.ToRoot ->
                    Core.Behaviors.core<Protocol>()(actor, message)
                else ->
                    actor.same()
            }
        }

    private fun protocol(
        directory: ActorReference<Directory.Protocol>,
        deadLetter: ActorReference<DeadLetter.Protocol>
    ): ProtocolBehavior<Protocol> =
        { actor, message ->
            when (message.content) {
                is ToDirectory ->
                    directory tell message.content.message
                is ToDeadLetter ->
                    deadLetter tell message.content.message
                is Install -> {
                    actor actorFor message.content.behavior
                    Unit
                }
                else ->
                    reject
            }.exhaustive

            actor.same()
        }

    fun new(): Behavior<Protocol> = Core.Behaviors.stashBehavior(init)

}