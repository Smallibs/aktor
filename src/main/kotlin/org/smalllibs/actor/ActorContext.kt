package org.smalllibs.actor

interface ActorContext<T> {

    fun self(): ActorReference<T>

    fun parent(): ActorReference<*>?

    fun children(): List<ActorReference<*>>

}
