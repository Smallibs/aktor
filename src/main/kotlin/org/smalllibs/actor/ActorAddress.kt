package org.smalllibs.actor

interface ActorAddress<T> {

    fun path(): ActorPath

    infix fun childOf(address: ActorAddress<T>): Boolean = address parentOf this

    infix fun parentOf(address: ActorAddress<T>): Boolean

}
