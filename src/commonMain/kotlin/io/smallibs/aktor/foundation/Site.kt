package io.smallibs.aktor.foundation

import io.smallibs.aktor.*
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.core.Core.Behaviors
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject

class Site(val system: ActorReference<System.Protocol>, val user: ActorReference<User.Protocol>) :
    Behavior<Site.Protocol> {

    interface Protocol
    data class UserInstall<R>(val behavior: Behavior<R>) : Protocol
    data class SystemInstall<R>(val behavior: Behavior<R>) : Protocol

    override var core: CoreReceiver<Protocol> = { actor, message ->
        when (message.content) {
            is Core.Start -> {
                system tell message.content
                user tell message.content
            }
            is Core.Stopped ->
                system tell message.content
            else ->
                Behaviors.core(actor, message)
        }.exhaustive
    }

    override val protocol: ProtocolReceiver<Protocol> =
        { _, message ->
            when (message.content) {
                is SystemInstall<*> ->
                    system tell System.Install(message.content.behavior)
                is UserInstall<*> ->
                    user tell User.Install(message.content.behavior)
                else ->
                    reject
            }.exhaustive
        }

    companion object {
        fun new(system: ActorReference<System.Protocol>, user: ActorReference<User.Protocol>): Site =
            Site(system, user)
    }
}

class SiteActor(private val actor: Actor<Site.Protocol>, private val site: Site) : Actor<Site.Protocol> by actor {
    val system = site.system
    val user = site.user
}
