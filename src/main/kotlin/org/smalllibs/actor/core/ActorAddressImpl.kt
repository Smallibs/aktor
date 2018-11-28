package org.smalllibs.actor.core

import org.smalllibs.actor.ActorAddress
import java.util.*

data class ActorAddressImpl(override val name: String, override val parent: ActorAddress? = null) : ActorAddress {

    override fun parentOf(address: ActorAddress): Boolean =
        address.parent?.let { this === it } ?: false

    private fun freshName(): String =
        UUID.randomUUID().toString()

    override fun toString(): String {
        return "${parent ?: ""}/$name"
    }

    fun newChild(name: String?): ActorAddressImpl =
        ActorAddressImpl(name ?: freshName(), this)

}
