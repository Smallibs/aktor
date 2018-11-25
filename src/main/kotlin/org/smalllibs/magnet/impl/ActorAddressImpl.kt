package org.smalllibs.magnet.impl

import org.smalllibs.magnet.ActorAddress

data class ActorAddressImpl<T>(private val path: ActorPathImpl) : ActorAddress<T> {

    override fun path(): ActorPathImpl = path

    override fun parentOf(address: ActorAddress<T>): Boolean =
            address.path().parent()?.let { this.path == it } ?: false

}
