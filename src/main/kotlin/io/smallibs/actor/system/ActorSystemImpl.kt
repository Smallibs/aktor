package io.smallibs.actor.system

import io.smallibs.actor.ActorReference
import io.smallibs.actor.ActorSystem
import io.smallibs.actor.Behavior
import io.smallibs.actor.core.ActorAddressImpl
import io.smallibs.actor.core.ActorImpl
import io.smallibs.actor.core.ActorReferenceImpl
import io.smallibs.actor.engine.ActorDispatcher
import io.smallibs.actor.engine.ActorRunner

class ActorSystemImpl(site: String, execution: ActorRunner) : ActorSystem {

    private val dispatcher: ActorDispatcher
    private val site: ActorImpl<Any>
    private val system: ActorImpl<Any>
    private val user: ActorImpl<Any>

    init {
        this.dispatcher = ActorDispatcher(execution)
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
