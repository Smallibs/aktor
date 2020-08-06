package io.smallibs.aktor

import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class SimpleActorTest {

    @Test
    fun shouldBeCalled() {
        val system = ActorSystem.system("test")

        val called = AtomicBoolean(false)
        val reference = system.actorFor<Boolean> { _, _ -> called.set(true) }

        reference tell true

        await().atMost(FIVE_SECONDS).until {
            called.get()
        }
    }

    @Test
    fun shouldBeCalledWithTheCorrectValue() {
        val system = ActorSystem.system("test")

        val called = AtomicInteger(0)
        val reference = system.actorFor<Int> { _, m -> called.set(m.content) }

        reference tell 42

        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
    }

    @Test
    fun shouldPerformActorTellChain() {
        val system = ActorSystem.system("test")

        val called = AtomicInteger(0)
        val secondary = system.actorFor<Int> { _, m -> called.set(m.content) }
        val primary = system.actorFor<Int> { _, m -> secondary.tell(m) }

        primary tell 42

        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
    }
}
