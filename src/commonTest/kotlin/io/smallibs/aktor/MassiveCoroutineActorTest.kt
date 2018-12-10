package io.smallibs.aktor

import io.smallibs.concurrent.AtomicReference
import io.smallibs.concurrent.incrementAndGet
import kotlin.test.Test

private const val actors = 1000
private const val messages = 1000

class MassiveActorTest {

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val system = ActorSystem.system("test", execution = ActorRunner.coroutine())

        var called = AtomicReference(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { _, _ -> called.incrementAndGet() }
        }

        repeat(messages) {
            references.forEach { a -> a tell true }
        }

        /*
        await().atMost(FIVE_SECONDS).until {
            called.get() == messages * actors
        }
        */
    }

}