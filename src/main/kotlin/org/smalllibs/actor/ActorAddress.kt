package org.smalllibs.actor

interface ActorAddress {

    val path: ActorPath

    infix fun childOf(address: ActorAddress): Boolean = address parentOf this

    infix fun parentOf(address: ActorAddress): Boolean

}
