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

    fun new(siteName: String, execution: ActorRunner = ActorRunner.coroutine()): SiteActor {
        val dispatcher = ActorDispatcher(execution)

        val addressSite = ActorAddressImpl(siteName)
        val referenceSite = ActorReferenceImpl<Site.Protocol>(dispatcher, addressSite)

        val addressSystem = ActorAddressImpl("system", addressSite)
        val referenceSystem = ActorReferenceImpl<System.Protocol>(dispatcher, addressSystem)

        val addressUser = ActorAddressImpl("user", addressSite)
        val referenceUser = ActorReferenceImpl<User.Protocol>(dispatcher, addressUser)

        val site = Site.new(referenceSystem, referenceUser)

        dispatcher.register(referenceSystem, System.new())
        dispatcher.register(referenceUser, User.new())

        val actorSite = dispatcher.register(referenceSite, site)

        referenceSite tell Core.Start

        return SiteActor(actorSite, site)
    }

}
