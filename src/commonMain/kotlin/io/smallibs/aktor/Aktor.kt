package io.smallibs.aktor

import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.foundation.Site
import io.smallibs.aktor.foundation.System
import io.smallibs.aktor.foundation.User
import io.smallibs.aktor.utils.Names

data class AktorSystem(
    private val site: Actor<Site.Protocol>,
    private val system: Actor<System.Protocol>,
    private val user: Actor<User.Protocol>
) : ActorBuilder by user, ActorReference<User.Protocol> by user.context.self {

    fun haltSystem() = site.context.self tell Core.Kill

}

object Aktor {

    fun new(siteName: String? = null, execution: ActorRunner = ActorRunner.coroutine()): AktorSystem {
        val dispatcher = ActorDispatcher(execution)

        val addressSite = ActorAddressImpl(siteName ?: Names.generate())

        val referenceSite = ActorReferenceImpl<Site.Protocol>(dispatcher, addressSite)
        val referenceSystem = ActorReferenceImpl<System.Protocol>(dispatcher, addressSite.newChild(System.name))
        val referenceUser = ActorReferenceImpl<User.Protocol>(dispatcher, addressSite.newChild(User.name))

        val site = dispatcher.register(referenceSite, Site.new(referenceSystem, referenceUser))
        val system = dispatcher.register(referenceSystem, System.new())
        val user = dispatcher.register(referenceUser, User.new())

        return AktorSystem(site, system, user)
    }

}
