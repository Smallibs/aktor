package org.smalllibs.actor.system

import org.smalllibs.actor.*
import org.smalllibs.actor.engine.ActorDispatcher
import org.smalllibs.actor.reference.ActorAddressImpl
import org.smalllibs.actor.reference.ActorPathImpl
import org.smalllibs.actor.reference.ActorReferenceImpl

class ActorSystemImpl(site: String, execution: ActorExecution) : ActorSystem {

    private val root: Actor<Any>

    init {
        this.root = root(site, execution)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R> {
        return root.actorFor(behavior, name)
    }

    private fun root(site: String, execution: ActorExecution): Actor<Any> {
        val dispatcher = ActorDispatcher(execution)
        val path = ActorPathImpl(site)
        val address = ActorAddressImpl<Any>(path)
        val reference = ActorReferenceImpl(dispatcher, address)

        return dispatcher.register(reference) { _, _ -> }
    }

}
