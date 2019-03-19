package io.smallibs.aktor.core

import io.smallibs.aktor.ActorRunner
import io.smallibs.aktor.Aktor
import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test

private const val actors = 1000
private const val messages = 1000

class MassiveActorTest {

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val system =
            Aktor.new("test", execution = ActorRunner.coroutine())

        val called = atomic(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { a, _ ->
                called.incrementAndGet()
                a.behavior()
            }
        }

        repeat(messages) {
            references.forEach { a -> a tell true }
        }

        Await(5000).until { called.value == messages * actors }
    }

}