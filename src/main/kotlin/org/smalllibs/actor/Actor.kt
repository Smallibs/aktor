package org.smalllibs.actor

interface Actor<T> : ActorFor {

    fun self(): ActorReference<T>

    fun behavior(): Behavior<T>

    fun start(behavior: Behavior<T>, stacked: Boolean)

    infix fun start(receiver: Receiver<T>) {
        start(Behavior(receiver))
    }

    infix fun start(behavior: Behavior<T>) {
        start(behavior, false)
    }

    fun start(receiver: Receiver<T>, stacked: Boolean) {
        start(Behavior(receiver), stacked)
    }

    fun finish()

}
