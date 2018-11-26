package org.smalllibs.actor

import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class BehaviorActorTest {

    @Test
    fun shouldBeCalledAndChangeTheBehavior() {
        val system = ActorSystem.system("test")

        val called = AtomicInteger(0)
        val reference = system.actorFor<Int> { a, v1 ->
            a start { _, v2 ->
                called.set(v1.content + v2.content)
            }
        }

        reference tell 12
        reference tell 30

        await().atMost(FIVE_SECONDS).until { called.get() == 42 }
    }

}