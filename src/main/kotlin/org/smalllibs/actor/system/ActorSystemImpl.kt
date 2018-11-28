package org.smalllibs.actor.system

import org.smalllibs.actor.ActorReference
import org.smalllibs.actor.ActorSystem
import org.smalllibs.actor.Behavior
import org.smalllibs.actor.core.ActorAddressImpl
import org.smalllibs.actor.core.ActorImpl
import org.smalllibs.actor.core.ActorReferenceImpl
import org.smalllibs.actor.engine.ActorDispatcher
import org.smalllibs.actor.engine.ActorExecutionImpl
import org.smalllibs.actor.engine.ActorRunner

class ActorSystemImpl(site: String, execution: ActorRunner) : ActorSystem {

    private val dispatcher: ActorDispatcher = ActorDispatcher(ActorExecutionImpl(execution))

    private val site: ActorImpl<Any>
    private val system: ActorImpl<Any>
    private val user: ActorImpl<Any>

    init {
        this.site = actor(site)
        this.system = actor("system", this.site.context.self.address)
        this.user = actor("user", this.site.context.self.address)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R> =
        this.user.actorFor(behavior, name)

    private fun actor(site: String, parent: ActorAddressImpl? = null): ActorImpl<Any> {
        val address = ActorAddressImpl(site, parent)
        val reference = ActorReferenceImpl<Any>(dispatcher, address)

        return dispatcher.register(reference) { _, _ -> }
    }
}
