package io.smallibs.aktor.core

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Envelop
import io.smallibs.aktor.foundation.DeadLetter
import io.smallibs.aktor.foundation.System
import io.smallibs.aktor.utils.NotExhaustive

class ActorImpl<T>(override val context: ActorContextImpl<T>) : Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()

    private val behaviors: MutableList<Behavior<T>> = mutableListOf()

    constructor(self: ActorReferenceImpl<T>, behavior: Behavior<T>) : this(ActorContextImpl(self)) {
        behaviors.add(behavior)
        behavior.onStart(this)
    }

    override fun behavior(): Behavior<T> =
        currentBehavior()

    override fun become(behavior: Behavior<T>, stacked: Boolean) {
        currentBehavior().also {
            if (stacked) {
                it.onPause(this)
            } else {
                removeBehavior()
                it.onFinish(this)
            }
        }

        addBehavior(behavior)
        behavior.onStart(this)
    }

    override fun unbecome() {
        currentBehavior().also {
            removeBehavior()
            it.onFinish(this)
            currentBehavior().onResume(this)
        }
    }

    override fun kill(): Boolean =
        context.self.unregister(context.self)

    override fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R> =
        context.self.register(behavior, name)

    //
    // Protected behaviors
    //

    internal fun deliver(envelop: Envelop<T>) =
        this.actorMailbox.deliver(envelop)

    internal fun nextTurn(): (() -> Unit)? =
        actorMailbox.next()?.let { envelop ->
            {
                try {
                    behavior().receive(this, envelop)
                } catch (e: NotExhaustive) {
                    context.self tell Core.Escalate(System.ToDeadLetter(DeadLetter.NotManaged(context.self, envelop)))
                }
            }
        }

    //
    // Private behaviors
    //

    private fun currentBehavior(): Behavior<T> =
        behaviors.getOrNull(0) ?: Behavior of { _, _ -> Unit }

    private fun removeBehavior() =
        behaviors.getOrNull(0)?.let { behaviors.removeAt(0) }

    private fun addBehavior(behavior: Behavior<T>) =
        behaviors.add(0, behavior)

}

