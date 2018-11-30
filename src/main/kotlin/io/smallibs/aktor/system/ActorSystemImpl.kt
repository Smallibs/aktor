package io.smallibs.aktor.system

import io.smallibs.aktor.ActorContext
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.ActorSystem
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.engine.ActorRunner

class ActorSystemImpl(site: String, execution: ActorRunner) : ActorSystem {

    private val dispatcher: ActorDispatcher = ActorDispatcher(execution)
    
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

    override val context: ActorContext<Any> get() = site.context

    override fun behavior(): Behavior<Any>? = site.behavior()

    override fun start(behavior: Behavior<Any>, stacked: Boolean) = site.start(behavior, stacked)

    override fun finish() = site.finish()

}
