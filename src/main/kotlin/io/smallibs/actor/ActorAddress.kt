package io.smallibs.actor

interface ActorAddress {

    val name: String

    val parent: ActorAddress?

    infix fun childOf(address: ActorAddress): Boolean = address parentOf this

    infix fun parentOf(address: ActorAddress): Boolean

}
