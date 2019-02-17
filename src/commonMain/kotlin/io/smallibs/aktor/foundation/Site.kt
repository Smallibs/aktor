package io.smallibs.aktor.foundation

import io.smallibs.aktor.*
import io.smallibs.aktor.core.Core.Behaviors
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

class Site(val system: ActorReference<System.Protocol>, val user: ActorReference<User.Protocol>) : Behavior<Site.Protocol> {

    interface Protocol
    data class UserInstall<R>(val behavior: Behavior<R>) : Protocol
    data class SystemInstall<R>(val behavior: Behavior<R>) : Protocol

    override var core: CoreReceiver<Protocol> = Behaviors.core

    override val protocol: ProtocolReceiver<Protocol> =
        { _, message ->
            when (message.content) {
                is SystemInstall<*> -> system tell System.Install(message.content.behavior)
                is UserInstall<*> -> user tell User.Install(message.content.behavior)
                else -> reject
            }.exhaustive
        }

    companion object {
        fun new(system: ActorReference<System.Protocol>, user: ActorReference<User.Protocol>): Site =
            Site(system, user)
    }
}

data class SiteActor(val actor: Actor<Site.Protocol>, private val site: Site) : Actor<Site.Protocol> by actor {
    val system = site.system
    val user = site.user
}
