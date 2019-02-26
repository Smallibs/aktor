package io.smallibs.aktor

import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.foundation.Site
import io.smallibs.aktor.foundation.SiteActor
import io.smallibs.aktor.foundation.System
import io.smallibs.aktor.foundation.User

object Aktor {

    fun new(siteName: String? = null, execution: ActorRunner = ActorRunner.coroutine()): SiteActor {
        val dispatcher = ActorDispatcher(execution)

        val addressSite = ActorAddressImpl(siteName ?: Names.generate())
        val referenceSite = ActorReferenceImpl<Site.Protocol>(dispatcher, addressSite)

        val addressSystem = ActorAddressImpl("system", addressSite)
        val referenceSystem = ActorReferenceImpl<System.Protocol>(dispatcher, addressSystem)
        dispatcher.register(referenceSystem, System.new())
        referenceSystem tell Core.Live

        val addressUser = ActorAddressImpl("user", addressSite)
        val referenceUser = ActorReferenceImpl<User.Protocol>(dispatcher, addressUser)
        dispatcher.register(referenceUser, User.new())
        referenceUser tell Core.Live

        val site = Site.new(referenceSystem, referenceUser)
        val actorSite = dispatcher.register(referenceSite, site)

        referenceSite tell Core.Live

        return SiteActor(actorSite, site)
    }

}