package io.smallibs.aktor

import io.smallibs.utils.Await
import kotlin.test.Test

class ActorBecomeTest {

    enum class Event {
        INITIALIZED, STARTED, RESUMED, PAUSED, FINISHED
    }

    enum class Order {
        BECOME, UNBECOME
    }

    class TestBehavior(val notifier: (Event) -> Unit) : Behavior<Order> {
        override val core: CoreReceiver<Order>
            get() = { _, _ -> Unit }
        override val protocol: ProtocolReceiver<Order>
            get() = { a, m -> receive(a, m) }

        override fun receive(actor: Actor<Order>, envelop: Envelop<Order>) =
            when (envelop) {
                is ProtocolEnvelop ->
                    when (envelop.content) {
                        Order.BECOME -> actor become this
                        Order.UNBECOME -> actor.unbecome()
                    }
                is CoreEnvelop -> Unit
            }

        override fun onStart(actor: Actor<Order>) {
            super.onStart(actor)
            notifier(Event.STARTED)
        }

        override fun onResume(actor: Actor<Order>) {
            super.onResume(actor)
            notifier(Event.RESUMED)
        }

        override fun onPause(actor: Actor<Order>) {
            super.onPause(actor)
            notifier(Event.PAUSED)
        }

        override fun onFinish(actor: Actor<Order>) {
            super.onFinish(actor)
            notifier(Event.FINISHED)
        }
    }

    @Test
    fun shouldAskAnActorBecome() {
        val system = Aktor.new("test")

        var called = listOf(Event.INITIALIZED)
        val reference = system actorFor TestBehavior { called = called + it }

        reference tell Order.BECOME

        Await(5000).until { called == listOf(Event.INITIALIZED, Event.STARTED) }

    }

    @Test
    fun shouldAskAnActorUnbecome() {
        val system = Aktor.new("test")

        var called = listOf(Event.INITIALIZED)
        val reference = system actorFor TestBehavior { called = called + it }

        reference tell Order.UNBECOME

        Await(5000).until { called == listOf(Event.INITIALIZED) }

    }

    @Test
    fun shouldAskAnActorBecomeAndUnbecome() {
        val system = Aktor.new("test")

        var called = listOf(Event.INITIALIZED)
        val reference = system actorFor TestBehavior { called = called + it }

        reference tell Order.BECOME
        reference tell Order.UNBECOME

        Await(5000).until { called == listOf(Event.INITIALIZED, Event.STARTED, Event.FINISHED, Event.RESUMED) }

    }

}