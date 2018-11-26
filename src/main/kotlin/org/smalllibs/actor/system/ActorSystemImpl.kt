package org.smalllibs.actor.system

import org.smalllibs.actor.Actor
import org.smalllibs.actor.ActorReference
import org.smalllibs.actor.ActorSystem
import org.smalllibs.actor.Behavior
import org.smalllibs.actor.engine.ActorDispatcher
import org.smalllibs.actor.engine.ActorExecutionImpl
import org.smalllibs.actor.engine.ActorRunner
import org.smalllibs.actor.reference.ActorAddressImpl
import org.smalllibs.actor.reference.ActorPathImpl
import org.smalllibs.actor.reference.ActorReferenceImpl

class ActorSystemImpl(site: String, execution: ActorRunner) : ActorSystem {

    private val root: Actor<Any>

    init {
        this.root = root(site, execution)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R> {
        return root.actorFor(behavior, name)
    }

    private fun root(site: String, execution: ActorRunner): Actor<Any> {
        val dispatcher = ActorDispatcher(ActorExecutionImpl(execution))
        val path = ActorPathImpl(site)
        val address = ActorAddressImpl<Any>(path)
        val reference = ActorReferenceImpl(dispatcher, address)

        return dispatcher.register(reference) { _, _ -> }
    }

}
