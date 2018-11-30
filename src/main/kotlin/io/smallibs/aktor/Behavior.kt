@file:Suppress("UNUSED_PARAMETER")

package io.smallibs.aktor

typealias Receiver<T> = (Actor<T>, Envelop<T>) -> Unit

class Behavior<T>(val receiver: Receiver<T>) {

    fun onStart(actor: Actor<T>) {}

    fun onResume(actor: Actor<T>) {}

    fun onPause(actor: Actor<T>) {}

    fun onStop(actor: Actor<T>) {}

}