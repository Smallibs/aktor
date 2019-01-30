package io.smallibs.aktor.core

import io.smallibs.aktor.SystemReceiver

object Behaviors {

    fun <T> system(): SystemReceiver<T> = { actor, message ->
        when (message.content) {
            is StopActor -> {
                actor.context.children().forEach { it tell StopActor }
                actor.finish()
                actor.context.parent()?.let { it tell StoppedActor(actor.context.self) }
            }
            is StoppedActor -> {
                TODO()
            }
        }
    }

}