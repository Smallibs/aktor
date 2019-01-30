package io.smallibs.aktor

interface Actor<T> : ActorBuilder {

    val context: ActorContext<T>

    fun behavior(): Behavior<T>

    fun become(behavior: Behavior<T>, stacked: Boolean)

    infix fun become(receiver: Receiver<T>) {
        become(Behavior of receiver)
    }

    infix fun become(behavior: Behavior<T>) {
        become(behavior, false)
    }

    fun become(receiver: Receiver<T>, stacked: Boolean) {
        become(Behavior of receiver, stacked)
    }

    fun unbecome()

    fun finish()

}
