@file:Suppress("UNUSED_PARAMETER")

package io.smallibs.aktor

import io.smallibs.aktor.core.Behaviors.system

typealias Receiver<T> = (Actor<T>, ProtocolEnvelop<T>) -> Unit
typealias SystemReceiver<T> = (Actor<T>, SystemEnvelop<T>) -> Unit

interface Behavior<T> {

    fun receive(actor: Actor<T>, envelop: Envelop<T>)

    fun onStart(actor: Actor<T>) {}

    fun onResume(actor: Actor<T>) {}

    fun onPause(actor: Actor<T>) {}

    fun onFinish(actor: Actor<T>) {}

    companion object {
        infix fun <T> of(receiver: Receiver<T>): Behavior<T> =
            ForReceiver(receiver)
    }

    class ForReceiver<T>(private val receiver: Receiver<T>) : Behavior<T> {
        override fun receive(actor: Actor<T>, envelop: Envelop<T>) =
            when (envelop) {
                is ProtocolEnvelop<T> -> receiver(actor, envelop)
                is SystemEnvelop<T> -> system<T>()(actor, envelop)
            }
    }

}