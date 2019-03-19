package io.smallibs.aktor.core

import io.smallibs.aktor.*
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
                            actor.context.children().forEach { it tell Core.Kill }
                            actor.context.parent()?.let { it tell Core.Killed(actor.context.self) }
                        } else {
                            Unit
                        }
                    }
                    is Killed -> {
                        actor.context.parent()?.let { it tell message.content }
                    }
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
            val stashed: Stashed<T> = Stashed(listOf())
        ) : Behavior<T> {

            override val core: CoreBehavior<T> get() = coreBehavior

            override val protocol: ProtocolBehavior<T> = { actor, message ->
                try {
                    behavior(actor, message)
                } catch (e: NotExhaustive) {
                    if (!stashed.stash(message.content)) {
                        throw NotExhaustive() // TODO
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

