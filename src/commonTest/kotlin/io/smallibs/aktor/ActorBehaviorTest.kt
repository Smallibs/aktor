package io.smallibs.aktor

import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class ActorBehaviorTest {

    @Test
    fun shouldBeCalledAndStartABehavior() {

        val system = Bootstrap.new("test")

        val called = atomic(0)
        val reference = system.actorFor<Int> { a, v1 ->
            a become { _, v2 ->
                called.getAndSet(v1.content + v2.content)
            }
        }

        reference tell 12
        reference tell 30

        Await(5000).until { called.value == 42 }
    }

    @Test
    fun shouldBeCalledAndStartAndFinishABehavior() {
        val system = Bootstrap.new("test")

        val done = atomic(false)
        val called = atomic(0)
        val reference = system.actorFor<Int> { a, v1 ->
            if (done.value) {
                called.addAndGet(v1.content)
            } else {
                a.become({ _, v2 ->
                    done.getAndSet(true)
                    called.addAndGet(v1.content + v2.content)
                    a.unbecome()
                }, true)
            }
        }

        reference tell 12
        reference tell 15
        reference tell 15

        Await(5000).until { called.value == 42 }
    }
}
