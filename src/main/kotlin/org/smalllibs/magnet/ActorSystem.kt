package org.smalllibs.magnet

import org.smalllibs.magnet.impl.ActorSystemImpl

interface ActorSystem {

    infix fun <R> actorFor(receiver: Receiver<R>) = actorFor(Behavior(receiver))

    infix fun <R> actorFor(behavior: Behavior<R>) = actorFor(behavior, null)

    fun <R> actorFor(behavior: Behavior<R>, name: String? = null): ActorReference<R>

    companion object {
        fun system(site: String): ActorSystem {
            return ActorSystemImpl(site)
        }
    }

}
