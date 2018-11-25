package org.smalllibs.magnet.impl

import org.smalllibs.magnet.Actor
import org.smalllibs.magnet.ActorReference
import org.smalllibs.magnet.Behavior
import org.smalllibs.magnet.Envelop
import java.util.*

class ActorImpl<T> internal constructor(private val reference: ActorReferenceImpl<T>) : Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()
    private val behaviors: Stack<Behavior<T>> = Stack()

    override fun self(): ActorReference<T> {
        return this.reference
    }

    override fun behavior(): Behavior<T> {
        return behaviors.peek()
    }

    override fun start(behavior: Behavior<T>, stacked: Boolean) {
        currentBehavior()?.let {
            it.onStop()
            if (!stacked) {
                behaviors.pop()
                behavior.onFinish()
            } else {
                behavior.onStop()
            }
        }

        behaviors.push(behavior)
        behavior.onStart()
    }

    override fun finish() {
        currentBehavior()?.let {
            behaviors.pop()
            it.onStop()
        }

        currentBehavior()?.let { it.onResume() }
    }

    override fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R> =
            reference.register(behavior, name)

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

    private fun currentBehavior(): Behavior<T>? =
            if (!behaviors.isEmpty()) {
                behaviors.peek()
            } else {
                null
            }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val actor = other as ActorImpl<*>?
        return reference == actor!!.reference
    }

    override fun hashCode(): Int {
        return Objects.hash(reference)
    }
}
