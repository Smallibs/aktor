package io.smallibs.aktor.core

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Envelop

class ActorImpl<T> private constructor(override val context: ActorContextImpl<T>) : Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()
    private val behaviors: MutableList<Behavior<T>> = mutableListOf()

    constructor(self: ActorReferenceImpl<T>, behavior: Behavior<T>) : this(ActorContextImpl(self)) {
        this.start(behavior)
    }

    override fun behavior(): Behavior<T>? = currentBehavior()

    override fun start(behavior: Behavior<T>, stacked: Boolean) {
        currentBehavior()?.let {
            it.onPause(this)
            if (!stacked) {
                removeCurrentBehavior()
                behavior.onStop(this)
            } else {
                behavior.onPause(this)
            }
        }

        setCurrentBehavior(behavior)
        behavior.onStart(this)
    }

    override fun finish() {
        currentBehavior()?.let {
            removeCurrentBehavior()
            it.onStop(this)
        }

        currentBehavior()?.onResume(this)
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R> =
        context.self.register(behavior, name)

    //
    // Protected behaviors
    //

    internal fun deliver(envelop: Envelop<T>) =
        this.actorMailbox.deliver(envelop)

    internal fun nextTurn(): (() -> Unit)? =
        actorMailbox.next()?.let { envelop -> { behavior()?.let { it.receive(this, envelop) } } }

    //
    // Private behaviors
    //

    private fun currentBehavior(): Behavior<T>? =
        behaviors.getOrNull(0)


    private fun removeCurrentBehavior() =
        behaviors.removeAt(0)

    private fun setCurrentBehavior(behavior: Behavior<T>) =
        behaviors.add(0, behavior)

}

