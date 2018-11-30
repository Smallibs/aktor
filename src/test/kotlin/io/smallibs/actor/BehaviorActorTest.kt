package io.smallibs.actor

import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import io.smallibs.actor.ActorSystem
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class BehaviorActorTest {

    @Test
    fun shouldBeCalledAndStartABehavior() {
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

    @Test
    fun shouldBeCalledAndStartAndFinishABehavior() {
        val system = ActorSystem.system("test")

        val done = AtomicBoolean(false)
        val called = AtomicInteger(0)
        val reference = system.actorFor<Int> { a, v1 ->
            if (done.get()) {
                called.addAndGet(v1.content)
            } else {
                a.start({ _, v2 ->
                    done.set(true)
                    called.addAndGet(v1.content + v2.content)
                    a.finish()
                }, true)
            }
        }

        reference tell 12
        reference tell 15
        reference tell 15

        await().atMost(FIVE_SECONDS).until { called.get() == 42 }
    }
}