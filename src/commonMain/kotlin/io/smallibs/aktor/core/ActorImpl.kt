package io.smallibs.aktor.core

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Envelop

class ActorImpl<T>(override val context: ActorContextImpl<T>, private val initial: Behavior<T>) : Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()

    private val behaviors: MutableList<Behavior<T>> = mutableListOf()

    constructor(self: ActorReferenceImpl<T>, behavior: Behavior<T>) : this(ActorContextImpl(self), behavior)

    override fun behavior(): Behavior<T> =
        currentBehavior()

    override fun become(behavior: Behavior<T>, stacked: Boolean) {
        currentStackedBehavior()?.let {
            if (stacked) {
                it.onPause(this)
            } else {
                removeStackedBehavior()
                it.onFinish(this)
            }
        }

        stackToCurrentBehaviors(behavior)
        behavior.onStart(this)
    }

    override fun unbecome() {
        currentStackedBehavior()?.let {
            removeStackedBehavior()
            it.onFinish(this)
            currentBehavior().onResume(this)
        }

    }

    override fun finish() =
        this.context.self tell Core.Stop

    override fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R> =
        context.self.register(behavior, name)

    //
    // Protected behaviors
    //

    internal fun deliver(envelop: Envelop<T>) =
        this.actorMailbox.deliver(envelop)

    internal fun nextTurn(): (() -> Unit)? =
        actorMailbox.next()?.let { envelop -> { behavior().receive(this, envelop) } }

    //
    // Private behaviors
    //

    private fun currentStackedBehavior(): Behavior<T>? =
        behaviors.getOrNull(0)

    private fun removeStackedBehavior() =
        behaviors.getOrNull(0)?.let { behaviors.removeAt(0) }

    private fun stackToCurrentBehaviors(behavior: Behavior<T>) =
        behaviors.add(0, behavior)

    private fun currentBehavior(): Behavior<T> =
        currentStackedBehavior() ?: initial

}

