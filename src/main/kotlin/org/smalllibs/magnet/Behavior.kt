package org.smalllibs.magnet

typealias Receiver<T> = (Actor<T>, Envelop<T>) -> Unit

class Behavior<T>(val receiver: Receiver<T>) {

    fun onStart() {}

    fun onResume() {}

    fun onStop() {}

    fun onFinish() {}

}