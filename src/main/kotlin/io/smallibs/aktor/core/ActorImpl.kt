package io.smallibs.aktor.core

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Envelop
import java.util.*

class ActorImpl<T> private constructor(override val context: ActorContextImpl<T>) :
    Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()
    private val behaviors: Stack<Behavior<T>> = Stack()

    constructor(self: ActorReferenceImpl<T>, behavior: Behavior<T>) : this(ActorContextImpl(self)) {
        this.start(behavior)
    }

    override fun behavior(): Behavior<T>? = currentBehavior()

    override fun start(behavior: Behavior<T>, stacked: Boolean) {
        currentBehavior()?.let {
            it.onPause(this)
            if (!stacked) {
                behaviors.pop()
                behavior.onStop(this)
            } else {
                behavior.onPause(this)
            }
        }

        behaviors.push(behavior)
        behavior.onStart(this)
    }

    override fun finish() {
        currentBehavior()?.let {
            behaviors.pop()
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
        actorMailbox.next()?.let { envelop -> { behavior()?.let { it.receiver(this, envelop) } } }

    //
    // Private behaviors
    //

    private fun currentBehavior(): Behavior<T>? =
        if (!behaviors.isEmpty()) {
            behaviors.peek()
        } else {
            null
        }

}

