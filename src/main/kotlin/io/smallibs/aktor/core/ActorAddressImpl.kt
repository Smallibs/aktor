package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress
import java.util.*

data class ActorAddressImpl(override val name: String, override val parent: ActorAddress? = null) :
    ActorAddress {

    override fun parentOf(address: ActorAddress): Boolean =
        address.parent?.let { this === it } ?: false

    private fun freshName(): String =
        UUID.randomUUID().toString()

    override fun toString(): String =
        "${parent ?: ""}/$name"

    fun newChild(name: String?): ActorAddressImpl =
        ActorAddressImpl(name ?: freshName(), this)

}
