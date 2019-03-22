package io.smallibs.aktor

import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.foundation.Site

object Aktor {

    fun new(siteName: String? = null, execution: ActorRunner = ActorRunner.coroutine()): Actor<Site.Protocol> {
        val dispatcher = ActorDispatcher(execution)

        val addressSite = ActorAddressImpl(siteName ?: Names.generate())
        val referenceSite = ActorReferenceImpl<Site.Protocol>(dispatcher, addressSite)

        val actorSite = dispatcher.register(referenceSite, Site.new())



        return actorSite
    }

}
