package org.smalllibs.magnet

interface Actor<T> {

    fun self(): ActorReference<T>

    fun behavior(): Behavior<T>

    fun start(behavior: Behavior<T>) {
        start(behavior, false)
    }

    fun start(behavior: Behavior<T>, stacked: Boolean)

    fun finish()

    fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R>

    fun <R> actorFor(behavior: Behavior<R>): ActorReference<R> {
        return actorFor(behavior, null)
    }

}
