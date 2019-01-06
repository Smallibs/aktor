package io.smallibs.aktor


import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertTrue

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

        Await(5000).until { called.value == 42 }
    }

    @Test
    fun shouldPerformActorTellChain() {
        val system = ActorSystem.system("test")

        val called = atomic(0)
        val secondary = system.actorFor<Int> { _, m -> called.getAndSet(m.content) }
        val primary = system.actorFor<Int> { _, m -> secondary.tell(m) }

        primary tell 42

        Await(5000).until { called.value == 42 }
    }

}