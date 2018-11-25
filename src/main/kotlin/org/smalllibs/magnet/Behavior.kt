package org.smalllibs.magnet

typealias Receive<T> = (Actor<T>, Envelop<T>) -> Unit

class Behavior<T>(val receive: Receive<T>) {

    fun onStart() {}

    fun onResume() {}

    fun onStop() {}

    fun onFinish() {}

}