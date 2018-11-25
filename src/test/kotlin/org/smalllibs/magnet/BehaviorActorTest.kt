package org.smalllibs.magnet


import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class BehaviorActorTest {

    @Test
    fun shouldBeCalledAndChangeTheBehavior() {
        val system = ActorSystem.system("test")

        val called = AtomicInteger(0)
        val reference = system.actorFor<Int> { a, _ ->
            a become { _, v -> called.set(v.content) }
        }

        reference tell 1
        reference tell 42

        await().atMost(FIVE_SECONDS).until { called.get() == 42 }
    }

}