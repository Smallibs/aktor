package io.smallibs.aktor

interface ActorProperty<T> {

    infix fun install(context: ActorBuilder): ActorReference<T>
}
