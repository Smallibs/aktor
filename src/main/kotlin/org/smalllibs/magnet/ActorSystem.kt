package org.smalllibs.magnet

import org.smalllibs.magnet.impl.ActorSystemImpl

interface ActorSystem {

    fun <R> actorFor(behavior: Behavior<R>, name: String? = null): ActorReference<R>

    infix fun <R> actorFor(behavior: Behavior<R>) =
        actorFor(behavior, null)

    infix fun <R> actorFor(receiver: Receiver<R>) =
        actorFor(receiver, null)

    fun <R> actorFor(receiver: Receiver<R>, name: String? = null): ActorReference<R> =
        actorFor(Behavior(receiver), name)

    companion object {
        fun system(site: String): ActorSystem {
            return ActorSystemImpl(site)
        }
    }

}
