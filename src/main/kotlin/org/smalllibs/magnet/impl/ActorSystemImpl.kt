package org.smalllibs.magnet.impl

import org.smalllibs.magnet.Actor
import org.smalllibs.magnet.ActorReference
import org.smalllibs.magnet.ActorSystem
import org.smalllibs.magnet.Behavior

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
