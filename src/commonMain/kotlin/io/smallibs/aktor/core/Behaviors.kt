package io.smallibs.aktor.core

import io.smallibs.aktor.SystemReceiver

object Behaviors {

    fun <T> system(): SystemReceiver<T> = { actor, message ->
        when (message.content) {
            is System.StopActor -> {
                actor.context.children().forEach { it tell System.StopActor }
                actor.finish()
                actor.context.parent()?.let { it tell System.StoppedActor(actor.context.self) }
            }
            is System.StoppedActor -> {
                // Bubbling message from actor to parent
                actor.context.parent()?.let { it tell message.content }
            }
        }
    }

}