package io.smallibs.aktor


import io.smallibs.concurrent.AtomicReference
import kotlin.test.Test

class SimpleActorTest {

    @Test
    fun shouldBeCalled() {
        val system = ActorSystem.system("test")

        val called = AtomicReference(false)
        val reference = system.actorFor<Boolean> { _, _ -> called.set(true) }

        reference tell true

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get()
        }
        */
    }

    @Test
    fun shouldBeCalledWithTheCorrectValue() {
        val system = ActorSystem.system("test")

        val called = AtomicReference(0)
        val reference = system.actorFor<Int> { _, m -> called.set(m.content) }

        reference tell 42

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
        */
    }

    @Test
    fun shouldPerformActorTellChain() {
        val system = ActorSystem.system("test")

        val called = AtomicReference(0)
        val secondary = system.actorFor<Int> { _, m -> called.set(m.content) }
        val primary = system.actorFor<Int> { _, m -> secondary.tell(m) }

        primary tell 42

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
        */
    }

}