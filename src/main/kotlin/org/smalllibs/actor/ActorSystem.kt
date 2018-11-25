package org.smalllibs.actor

import org.smalllibs.actor.impl.ActorSystemImpl

interface ActorSystem {

    infix fun <R> actorFor(receiver: Receiver<R>) =
        actorFor(receiver, null)

    fun <R> actorFor(receiver: Receiver<R>, name: String? = null): ActorReference<R> =
        actorFor(Behavior(receiver), name)

    infix fun <R> actorFor(behavior: Behavior<R>) =
        actorFor(behavior, null)

    fun <R> actorFor(behavior: Behavior<R>, name: String? = null): ActorReference<R>

    companion object {
        fun system(site: String): ActorSystem {
            return ActorSystemImpl(site)
        }
    }

}
