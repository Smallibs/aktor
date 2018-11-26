package org.smalllibs.actor.impl

import org.smalllibs.actor.Actor
import org.smalllibs.actor.ActorReference
import org.smalllibs.actor.ActorSystem
import org.smalllibs.actor.Behavior
import org.smalllibs.actor.engine.ActorDispatcher

class ActorSystemImpl(site: String) : ActorSystem {

    private val root: Actor<Any>

    init {
        this.root = root(site)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R> {
        return root.actorFor(behavior, name)
    }

    private fun root(site: String): Actor<Any> {
        val dispatcher = ActorDispatcher()
        val path = ActorPathImpl(site)
        val address = ActorAddressImpl<Any>(path)
        val reference = ActorReferenceImpl(dispatcher, address)

        return dispatcher.register(reference) { _, _ -> }
    }

}
