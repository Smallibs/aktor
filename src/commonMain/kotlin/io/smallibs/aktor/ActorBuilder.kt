package io.smallibs.aktor

interface  ActorBuilder {

    infix fun <R> actorFor(receiver: Receiver<R>): ActorReference<R> =
        actorFor(receiver, Names.generate())

    fun <R> actorFor(receiver: Receiver<R>, name: String): ActorReference<R> =
        actorFor(Behavior of receiver, name)

    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R> =
        actorFor(behavior, Names.generate())

    fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R>

}
