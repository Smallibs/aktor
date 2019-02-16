package io.smallibs.aktor.bootstrap

import io.smallibs.aktor.*
import io.smallibs.aktor.core.ActorAddressImpl
import io.smallibs.aktor.core.ActorReferenceImpl
import io.smallibs.aktor.engine.ActorDispatcher
import io.smallibs.aktor.foundation.Site

class Bootstrap(val site: Actor<Site.Protocol>) : Actor<Site.Protocol> by site {

    init {
        site.context.self tell Site.Start
    }

    companion object {
        fun new(site: String, execution: ActorRunner): Bootstrap {
            val dispatcher = ActorDispatcher(execution)
            val address = ActorAddressImpl(site)
            val reference = ActorReferenceImpl<Site.Protocol>(dispatcher, address)
            val actor = dispatcher.register(reference, Site.new())

            return Bootstrap(actor)
        }
    }

}
