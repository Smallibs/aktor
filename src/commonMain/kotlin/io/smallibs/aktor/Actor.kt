package io.smallibs.aktor

interface Actor<T> : ActorBuilder {

    val context: ActorContext<T>

    fun behavior(): Behavior<T>

    fun kill() : Boolean

}
