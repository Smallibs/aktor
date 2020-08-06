package io.smallibs.aktor.samples


import io.smallibs.aktor.Aktor
import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class SimpleActorTest {

    @Test
    fun shouldBeCalledWithTheCorrectValue() {
        val aktor = Aktor.new("test")

        val called = atomic(0)
        val reference = aktor.actorFor<Int> { a, m ->
            called.getAndSet(m.content); a.same()
        }

        reference tell 42

        Await() atMost 5000 until { called.value == 42 }

        aktor.halt()
    }

    @Test
    fun shouldBeCalledPreservingSequence() {
        val aktor = Aktor.new("test")

        val called = atomic(listOf<Int>())
        val reference = aktor.actorFor<Int> { a, m ->
            called.getAndSet(called.value + m.content);
            a.same()
        }

        reference tell 41
        reference tell 42
        reference tell 43

        Await() atMost 5000 until { called.value == listOf(41, 42, 43) }

        aktor.halt()
    }

    @Test
    fun shouldPerformActorTellChain() {
        val aktor = Aktor.new("test")

        val called = atomic(0)
        val secondary = aktor.actorFor<Int> { a, m ->
            called.getAndSet(m.content);
            a.same()
        }
        val primary = aktor.actorFor<Int> { a, m ->
            secondary.tell(m);
            a.same()
        }

        primary tell 42

        Await() atMost 5000 until { called.value == 42 }

        aktor.halt()
    }

}