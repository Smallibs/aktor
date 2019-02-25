package io.smallibs.aktor.samples


import io.smallibs.aktor.Aktor
import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class SimpleActorTest {

    @Test
    fun shouldBeCalledWithTheCorrectValue() {
        val system = Aktor.new("test")

        val called = atomic(0)
        val reference = system.actorFor<Int> { _, m -> called.getAndSet(m.content) }

        reference tell 42

        Await(5000).until { called.value == 42 }
    }

    @Test
    fun shouldBeCalledPreservingSequence() {
        val system = Aktor.new("test")

        val called = atomic(listOf<Int>())
        val reference = system.actorFor<Int> { _, m -> called.getAndSet(called.value + m.content) }

        reference tell 41
        reference tell 42
        reference tell 43

        Await(5000).until { called.value == listOf(41, 42, 43) }
    }

    @Test
    fun shouldPerformActorTellChain() {
        val system = Aktor.new("test")

        val called = atomic(0)
        val secondary = system.actorFor<Int> { _, m -> called.getAndSet(m.content) }
        val primary = system.actorFor<Int> { _, m -> secondary.tell(m) }

        primary tell 42

        Await(5000).until { called.value == 42 }
    }

}