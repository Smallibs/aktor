package io.smallibs.aktor

interface ActorContext<T> {

    val self: ActorReference<T>

    fun root(): ActorReference<*>

    fun parent(): ActorReference<*>?

    fun children(): Collection<ActorReference<*>>
}
