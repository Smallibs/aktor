package org.smalllibs.actor

interface ActorContext<T> {

    val self: ActorReference<T>

    fun parent(): ActorReference<*>?

    fun children(): List<ActorReference<*>>

}
