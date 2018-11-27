package org.smalllibs.actor.core

import org.smalllibs.actor.ActorContext
import org.smalllibs.actor.ActorReference

class ActorContextImpl<T>(private val self: ActorReferenceImpl<T>) : ActorContext<T> {

    override fun parent(): ActorReference<*>? = this.self.dispatcher.parent(self)

    override fun self(): ActorReferenceImpl<T> = this.self

    override fun children(): List<ActorReference<*>> = this.self.dispatcher.children(self)

}
