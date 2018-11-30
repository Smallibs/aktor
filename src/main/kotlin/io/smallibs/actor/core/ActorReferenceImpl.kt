package io.smallibs.actor.core

import io.smallibs.actor.ActorReference
import io.smallibs.actor.Behavior
import io.smallibs.actor.Envelop
import io.smallibs.actor.engine.ActorDispatcher

data class ActorReferenceImpl<T>(val dispatcher: ActorDispatcher, override val address: ActorAddressImpl) :
    ActorReference<T> {

    override fun tell(envelop: Envelop<T>) {
        this.dispatcher.deliver(this, envelop)
    }

    internal fun <R> register(behavior: Behavior<R>, name: String?): ActorReference<R> =
        dispatcher.register(newChild(name), behavior).context.self

    private fun <R> newChild(name: String?): ActorReferenceImpl<R> =
        ActorReferenceImpl(dispatcher, this.address.newChild(name))

}
