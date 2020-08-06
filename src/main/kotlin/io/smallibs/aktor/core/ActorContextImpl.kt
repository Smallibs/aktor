package io.smallibs.aktor.core

import io.smallibs.aktor.ActorContext
import io.smallibs.aktor.ActorReference

class ActorContextImpl<T>(override val self: ActorReferenceImpl<T>) :
    ActorContext<T> {

    override fun parent(): ActorReference<*>? =
        this.self.dispatcher.parent(self)

    override fun children(): Collection<ActorReference<*>> =
        this.self.dispatcher.children(self)
}
