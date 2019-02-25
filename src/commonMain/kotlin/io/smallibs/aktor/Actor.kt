package io.smallibs.aktor

interface Actor<T> : ActorBuilder {

    val context: ActorContext<T>

    fun behavior(): Behavior<T>

    fun become(behavior: Behavior<T>, stacked: Boolean)

    infix fun become(protocol: ProtocolReceiver<T>) {
        become(Behavior of Pair(behavior().core, protocol))
    }

    infix fun become(behavior: Behavior<T>) {
        become(behavior, false)
    }

    fun become(protocol: ProtocolReceiver<T>, stacked: Boolean) {
        become(Behavior of Pair(behavior().core, protocol), stacked)
    }

    fun unbecome()

    fun kill() : Boolean

}
