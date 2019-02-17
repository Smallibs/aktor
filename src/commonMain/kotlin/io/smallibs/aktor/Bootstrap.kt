package io.smallibs.aktor

import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.foundation.Site
import io.smallibs.aktor.foundation.SiteActor
import io.smallibs.aktor.foundation.System
import io.smallibs.aktor.foundation.User

object Bootstrap {

    fun new(siteName: String, execution: ActorRunner = ActorRunner.coroutine()): SiteActor {
        val dispatcher = ActorDispatcher(execution)

        val address = ActorAddressImpl(siteName)
        val reference = ActorReferenceImpl<Site.Protocol>(dispatcher, address)

        val addressSystem = ActorAddressImpl("system", address)
        val referenceSystem = ActorReferenceImpl<System.Protocol>(dispatcher, addressSystem)

        val addressUser = ActorAddressImpl("user", address)
        val referenceUser = ActorReferenceImpl<User.Protocol>(dispatcher, addressUser)

        val site = Site.new(referenceSystem, referenceUser)

        dispatcher.register(referenceSystem, System.new())
        dispatcher.register(referenceUser, User.new())

        return SiteActor(dispatcher.register(reference, site), site)
    }

}
