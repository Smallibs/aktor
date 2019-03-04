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

        val core: CoreReceiver<*> =
            { actor, message ->
                when (message.content) {
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
                        actor.context.root() tell message.content
                    else ->
                        reject
                }.exhaustive
            }

        fun <T> stashBehavior(
            coreReceiver: CoreReceiver<T>,
            behavior: ExhaustiveProtocolReceiver<T> = { _, _ -> reject.exhaustive }
        ): Behavior<T> =
            StashBehavior(coreReceiver, behavior)

        private class StashBehavior<T>(
            val coreReceiver: CoreReceiver<T>,
            val behavior: ExhaustiveProtocolReceiver<T>,
            val stashed: Stashed<T> = Stashed(listOf())
        ) : Behavior<T> {

            override val core: CoreReceiver<T> get() = coreReceiver

            override val protocol: ProtocolReceiver<T> = { actor, message ->
                try {
                    behavior(actor, message)
                } catch (e: NotExhaustive) {
                    if (!stashed.stash(message.content)) {
                        throw NotExhaustive() // TODO
                    }
                }
            }

            override fun onPause(actor: Actor<T>) {
                super.onPause(actor)
                stashed.unstashAll(actor.context.self)
            }

            override fun onFinish(actor: Actor<T>) {
                super.onFinish(actor)
                stashed.unstashAll(actor.context.self)
            }
        }

    }

}

