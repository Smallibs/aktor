package org.smalllibs.actor

typealias Receiver<T> = (Actor<T>, Envelop<T>) -> Unit

class Behavior<T>(val receiver: Receiver<T>) {

    fun onStart() {}

    fun onResume() {}

    fun onPause() {}

    fun onStop() {}

}