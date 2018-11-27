package org.smalllibs.actor

interface ActorBuilder {

    infix fun <R> actorFor(receiver: Receiver<R>): ActorReference<R> =
        actorFor(receiver, null)

    fun <R> actorFor(receiver: Receiver<R>, name: String?): ActorReference<R> =
        actorFor(Behavior(receiver), name)

    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R> =
        actorFor(behavior, null)

    fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R>

}
