package org.smalllibs.actor.reference

import org.smalllibs.actor.ActorAddress

data class ActorAddressImpl(override val path: ActorPathImpl) : ActorAddress {

    override fun parentOf(address: ActorAddress): Boolean =
        address.path.parent?.let { this.path === it } ?: false

}
