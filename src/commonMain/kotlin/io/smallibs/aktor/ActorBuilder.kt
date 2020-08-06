package io.smallibs.aktor

import io.smallibs.aktor.utils.Names

interface ActorBuilder {

    infix fun <R> actorFor(property: ActorProperty<R>): ActorReference<R> =
        property install this

    infix fun <R> actorFor(protocol: ProtocolBehavior<R>): ActorReference<R> =
        actorFor(protocol, Names.generate())

    fun <R> actorFor(protocol: ProtocolBehavior<R>, name: String): ActorReference<R> =
        actorFor(Behavior of protocol, name)

    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R> =
        actorFor(behavior, Names.generate())

    fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R>
}
