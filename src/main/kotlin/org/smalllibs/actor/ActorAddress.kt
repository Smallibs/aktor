package org.smalllibs.actor

interface ActorAddress<T> {

    val path: ActorPath

    infix fun childOf(address: ActorAddress<T>): Boolean = address parentOf this

    infix fun parentOf(address: ActorAddress<T>): Boolean

}
