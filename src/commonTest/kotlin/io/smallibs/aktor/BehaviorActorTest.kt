package io.smallibs.aktor

import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertTrue

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

        reference tell 12 tell 30

        Await(5000).until { called.value == 42 }
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

        reference tell 12 tell 15 tell 15

        Await(5000).until { called.value == 42 }
    }
}
