package io.smallibs.aktor

interface Actor<T> : ActorBuilder {

    val context: ActorContext<T>

    infix fun become(protocol: ProtocolBehavior<T>): Behavior<T>

    fun same(): Behavior<T>

    fun behavior(): Behavior<T> = same()

    fun kill(): Boolean

}

