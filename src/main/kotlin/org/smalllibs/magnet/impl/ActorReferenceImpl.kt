package org.smalllibs.magnet.impl

import org.smalllibs.magnet.ActorReference
import org.smalllibs.magnet.Behavior
import org.smalllibs.magnet.Envelop

data class ActorReferenceImpl<T> internal constructor(private val dispatcher: ActorDispatcher, private val address: ActorAddressImpl<T>) : ActorReference<T> {

    override fun address(): ActorAddressImpl<T> {
        return this.address
    }

    override fun tell(envelop: Envelop<T>) {
        this.dispatcher.deliver(address, envelop)
    }

    internal fun <R> register(behavior: Behavior<R>, name: String?): ActorReference<R> {
        return dispatcher.register(newChild(name), behavior).self()
    }

    private fun <R> newChild(name: String?): ActorReferenceImpl<R> {
        val actorPath = this.address().path().newChild(name)
        val address = ActorAddressImpl<R>(actorPath)

        return ActorReferenceImpl(dispatcher, address)
    }

}
