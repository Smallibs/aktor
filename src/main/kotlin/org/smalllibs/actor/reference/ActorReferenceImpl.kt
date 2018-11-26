package org.smalllibs.actor.reference

import org.smalllibs.actor.ActorReference
import org.smalllibs.actor.Behavior
import org.smalllibs.actor.Envelop
import org.smalllibs.actor.engine.ActorDispatcher

data class ActorReferenceImpl<T>(val dispatcher: ActorDispatcher, override val address: ActorAddressImpl<T>) :
    ActorReference<T> {

    override fun tell(envelop: Envelop<T>) {
        this.dispatcher.deliver(address, envelop)
    }

    internal fun <R> register(behavior: Behavior<R>, name: String?): ActorReference<R> =
        dispatcher.register(newChild(name), behavior).self()

    private fun <R> newChild(name: String?): ActorReferenceImpl<R> {
        val actorPath = this.address.path.newChild(name)
        val address = ActorAddressImpl<R>(actorPath)

        return ActorReferenceImpl(dispatcher, address)
    }

}
