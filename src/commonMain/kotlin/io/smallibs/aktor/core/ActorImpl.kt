package io.smallibs.aktor.core

import io.smallibs.aktor.*
import io.smallibs.aktor.foundation.DeadLetter
import io.smallibs.aktor.foundation.System
import io.smallibs.aktor.utils.NotExhaustive
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

class ActorImpl<T>(override val context: ActorContextImpl<T>, private val current: AtomicRef<Behavior<T>>) : Actor<T> {

    private val actorMailbox: ActorMailbox<T> = ActorMailbox()

    constructor(self: ActorReferenceImpl<T>, behavior: Behavior<T>) : this(ActorContextImpl(self), atomic(behavior)) {
        behavior.onStart(this)
    }

    override fun become(protocol: ProtocolBehavior<T>): Behavior<T> =
        Behavior.Companion.ForReceiver(same().core, protocol)

    override fun same(): Behavior<T> =
        this.current.value

    private fun become(behavior: Behavior<T>) {
        this.current.getAndSet(behavior).onStop(this)
        behavior.onStart(this)
    }

    override fun kill(): Boolean =
        context.self.unregister(context.self)

    override fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R> =
        context.self.register(behavior, name)

    //
    // Protected current
    //

    internal fun deliver(envelop: Envelop<T>) =
        this.actorMailbox.deliver(envelop)

    internal fun nextTurn(): (() -> Unit)? =
        actorMailbox.next()?.let { envelop ->
            {
                try {
                    this.become(current.value.receive(this, envelop))
                } catch (e: NotExhaustive) {
                    val message = DeadLetter.NotManaged(context.self, envelop, "message not processed")
                    this.context.self tell Core.ToRoot(System.ToDeadLetter(message))
                }
            }
        }
}

