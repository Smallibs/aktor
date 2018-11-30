package io.smallibs.actor.core

import io.smallibs.actor.ActorContext
import io.smallibs.actor.ActorReference

class ActorContextImpl<T>(override val self: ActorReferenceImpl<T>) :
    ActorContext<T> {

    override fun parent(): ActorReference<*>? = this.self.dispatcher.parent(self)

    override fun children(): Collection<ActorReference<*>> = this.self.dispatcher.children(self)

}
