package io.smallibs.aktor

import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertTrue

private const val actors = 1000
private const val messages = 1000

class MassiveActorTest {

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val system = ActorSystem.system("test", execution = ActorRunner.coroutine())

        var called = atomic(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { _, _ -> called.incrementAndGet() }
        }

        repeat(messages) {
            references.forEach { a -> a tell true }
        }

        assertTrue { Await.Until(500000) { called.value == messages * actors } }
    }

}