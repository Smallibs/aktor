package io.smallibs.aktor.core

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.CoreBehavior
import io.smallibs.aktor.ExhaustiveProtocolBehavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.foundation.Stashed
import io.smallibs.aktor.utils.NotExhaustive
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

object Core {

    interface Protocol
    object Live : Protocol
    object Kill : Protocol
    data class Killed(val reference: ActorReference<*>) : Protocol
    data class ToRoot(val message: Any) : Protocol

    object Behaviors {

        fun <T> core(): CoreBehavior<T> =
            { actor, message ->
                when (message.content) {
                    is Live ->
                        Unit
                    is Kill -> {
                        if (actor.kill()) {
                            actor.context.children().forEach { it tell Kill }
                            actor.context.parent()?.let { it tell Killed(actor.context.self) }
                        } else {
                            Unit
                        }
                    }
                    is Killed ->
                        actor.context.parent()?.let { it tell message.content }
                    is ToRoot ->
                        actor.context.parent()?.let { it tell message.content }
                    else ->
                        reject
                }.exhaustive

                actor.same()
            }

        fun <T> stashBehavior(
            coreBehavior: CoreBehavior<T>,
            behavior: ExhaustiveProtocolBehavior<T> = { _, _ -> reject.exhaustive }
        ): Behavior<T> =
            StashBehavior(coreBehavior, behavior)

        private class StashBehavior<T>(
            val coreBehavior: CoreBehavior<T>,
            val behavior: ExhaustiveProtocolBehavior<T>,
            val stashed: Stashed<T> = Stashed()
        ) : Behavior<T> {

            override val core: CoreBehavior<T> get() = coreBehavior

            override val protocol: ProtocolBehavior<T> = { actor, message ->
                try {
                    behavior(actor, message)
                } catch (e: NotExhaustive) {
                    if (!stashed.stash(message.content)) {
                        // Not enough space in the stashed actor
                        throw NotExhaustive() // TODO(didier) Add a specific exception
                    }
                }

                actor.same()
            }

            override fun onStop(actor: Actor<T>) {
                super.onStop(actor)
                stashed.unStashAll(actor.context.self)
            }

            override fun onKill(actor: Actor<T>) {
                super.onKill(actor)
                stashed.unStashAll(actor.context.self)
            }
        }
    }
}
