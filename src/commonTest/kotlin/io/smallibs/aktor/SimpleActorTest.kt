package io.smallibs.aktor


import kotlinx.atomicfu.atomic
import kotlin.test.Test

class SimpleActorTest {

    @Test
    fun shouldBeCalled() {
        val system = ActorSystem.system("test")

        val called = atomic(false)
        val reference = system.actorFor<Boolean> { _, _ -> called.getAndSet(true) }

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

        val called = atomic(0)
        val reference = system.actorFor<Int> { _, m -> called.getAndSet(m.content) }

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

        val called = atomic(0)
        val secondary = system.actorFor<Int> { _, m -> called.getAndSet(m.content) }
        val primary = system.actorFor<Int> { _, m -> secondary.tell(m) }

        primary tell 42

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
        */
    }

}