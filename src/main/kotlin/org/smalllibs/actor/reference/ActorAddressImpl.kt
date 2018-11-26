package org.smalllibs.actor.impl

import org.smalllibs.actor.ActorAddress

data class ActorAddressImpl<T>(override val path: ActorPathImpl) : ActorAddress<T> {

    override fun parentOf(address: ActorAddress<T>): Boolean =
        address.path.parent?.let { this.path === it } ?: false

}
