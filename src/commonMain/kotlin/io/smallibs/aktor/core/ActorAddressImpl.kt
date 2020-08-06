package io.smallibs.aktor.core

import io.smallibs.aktor.ActorAddress

data class ActorAddressImpl(
    override val name: String,
    override val parent: ActorAddress? = null
) : ActorAddress {

    override fun parentOf(address: ActorAddress): Boolean =
        address.parent?.let { this === it } ?: false

    override fun toString(): String =
        "${parent ?: ""}/$name"

    fun newChild(name: String): ActorAddressImpl =
        ActorAddressImpl(name, this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ActorAddressImpl

        if (name != other.name) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }
}
