package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress

data class ActorAddressImpl(override val name: String, override val parent: ActorAddress? = null) :
    ActorAddress {

    override fun parentOf(address: ActorAddress): Boolean =
        address.parent?.let { this === it } ?: false

    override fun toString(): String =
        "${parent ?: ""}/$name"

    fun newChild(name: String): ActorAddressImpl =
        ActorAddressImpl(name, this)
}
