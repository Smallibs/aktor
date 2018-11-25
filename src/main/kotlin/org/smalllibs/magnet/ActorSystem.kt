package org.smalllibs.magnet

import org.smalllibs.magnet.impl.ActorSystemImpl

interface ActorSystem {

    fun <R> actorFor(behavior: Behavior<R>, name: String? = null): ActorReference<R>

    fun <R> actorFor(receive: Receive<R>) = actorFor(Behavior(receive), null)

    companion object {
        fun system(site: String): ActorSystem {
            return ActorSystemImpl(site)
        }
    }

}
