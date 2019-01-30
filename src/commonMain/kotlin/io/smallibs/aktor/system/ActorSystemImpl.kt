package io.smallibs.aktor.system

import io.smallibs.aktor.*
import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.engine.ActorDispatcher

class ActorSystemImpl(site: String, execution: ActorRunner) : ActorSystem {

    private val dispatcher: ActorDispatcher = ActorDispatcher(execution)

    private val site: ActorImpl<Any>
    private val system: ActorImpl<Any>
    private val user: ActorImpl<Any>

    init {
        this.site = actor(site)
        this.system = actor("system", this.site)
        this.user = actor("user", this.site)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R> =
        this.user.actorFor(behavior, name)

    private fun actor(site: String, parent: ActorImpl<Any>? = null): ActorImpl<Any> {
        val address = ActorAddressImpl(site, parent?.let { context.self.address })
        val reference = ActorReferenceImpl<Any>(dispatcher, address)

        return dispatcher.register(reference) { _, _ -> }
    }

    override val context: ActorContext<Any>
        get() = site.context

    override fun behavior(): Behavior<Any> =
        site.behavior()

    override fun become(behavior: Behavior<Any>, stacked: Boolean) =
        site.become(behavior, stacked)

    override fun unbecome() =
        site.unbecome()

    override fun finish() {
        site.finish()
        // TODO End of the whole system
    }
}
