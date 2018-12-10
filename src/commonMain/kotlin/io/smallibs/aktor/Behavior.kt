@file:Suppress("UNUSED_PARAMETER")

package io.smallibs.aktor

typealias Receiver<T> = (Actor<T>, Envelop<T>) -> Unit

interface Behavior<T> {

    fun receive(actor: Actor<T>, envelop: Envelop<T>)

    fun onStart(actor: Actor<T>) {}

    fun onResume(actor: Actor<T>) {}

    fun onPause(actor: Actor<T>) {}

    fun onStop(actor: Actor<T>) {}

    companion object {
        infix fun <T> of(receiver: Receiver<T>): Behavior<T> =
            ForReceiver(receiver)
    }

    class ForReceiver<T>(private val receiver: Receiver<T>) : Behavior<T> {
        override fun receive(actor: Actor<T>, envelop: Envelop<T>) =
            receiver(actor, envelop)
    }

}