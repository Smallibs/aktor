package org.smalllibs.actor

interface Actor<T> {

    fun self(): ActorReference<T>

    fun behavior(): Behavior<T>

    fun become(behavior: Behavior<T>, stacked: Boolean)

    infix fun become(receiver: Receiver<T>) {
        become(Behavior(receiver))
    }

    infix fun become(behavior: Behavior<T>) {
        become(behavior, false)
    }

    fun become(receiver: Receiver<T>, stacked: Boolean) {
        become(Behavior(receiver), stacked)
    }

    fun unbecoming()

    infix fun <R> actorFor(receiver: Receiver<R>): ActorReference<R> =
        actorFor(receiver, null)

    fun <R> actorFor(receiver: Receiver<R>, name: String?): ActorReference<R> =
        actorFor(Behavior(receiver), name)

    fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R>


    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R> =
        actorFor(behavior, null)

}
