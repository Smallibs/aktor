package io.smallibs.aktor.core

import io.smallibs.aktor.Aktor
import io.smallibs.utils.Await
import io.smallibs.utils.System
import kotlinx.atomicfu.atomic
import kotlin.test.Test

private const val actors = 1_000
private const val messages = 1_000

class MassiveActorTest {

    private inline fun <T> stopWatch(label: () -> String, block: () -> T): T {
        val start = System.currentTimeMillis()
        val result = block()
        val duration = System.currentTimeMillis() - start
        println("${label()} done in $duration ms")
        return result
    }

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val aktor = Aktor.new("test")

        val called = atomic(0)

        val references = (0 until actors).map {
            aktor.actorFor<Boolean> { a, _ ->
                called.incrementAndGet()
                a.same()
            }
        }

        stopWatch({ "Execution of ${called.value} messages using Coroutines" }) {
            stopWatch({ "Submission" }) {
                repeat(messages) {
                    references.forEach { a -> a tell true }
                }
            }

            Await() atMost 5000 until { called.value == messages * actors }
        }
    }
}
