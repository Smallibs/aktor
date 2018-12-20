package io.smallibs.aktor

import kotlinx.atomicfu.atomic
import kotlin.test.Test

class BehaviorActorTest {

    @Test
    fun shouldBeCalledAndStartABehavior() {
        val system = ActorSystem.system("test")

        val called = atomic(0)
        val reference = system.actorFor<Int> { a, v1 ->
            a start { _, v2 ->
                called.getAndSet(v1.content + v2.content)
            }
        }

        reference tell 12
        reference tell 30

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
        */
    }

    @Test
    fun shouldBeCalledAndStartAndFinishABehavior() {
        val system = ActorSystem.system("test")

        val done = atomic(false)
        val called = atomic(0)
        val reference = system.actorFor<Int> { a, v1 ->
            if (done.value) {
                called.addAndGet(v1.content)
            } else {
                a.start({ _, v2 ->
                    done.getAndSet(true)
                    called.addAndGet(v1.content + v2.content)
                    a.finish()
                }, true)
            }
        }

        reference tell 12
        reference tell 15
        reference tell 15

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == 42
        }
        */
    }
}
