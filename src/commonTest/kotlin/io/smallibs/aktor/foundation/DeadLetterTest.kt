package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.foundation.Directory.searchByType
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class DeadLetterTest {

    object TestActor {
        interface Protocol
        object Dummy : Protocol

        val receiver: ProtocolBehavior<Protocol> = { a, _ ->
            reject.exhaustive
            a.same()
        }
    }

    @Test
    fun shouldBeNotifiedWhenAnActorDoesNotManageAndMessage() {
        val aktor = Aktor.new("site")
        val deadLetter = DeadLetter from aktor

        val atomic: AtomicRef<ActorReference<*>?> = atomic(null)
        deadLetter configure { reference, _ -> atomic.getAndSet(reference) }

        val test = aktor actorFor TestActor.receiver

        test tell TestActor.Dummy

        Await().atMost(5000).until { atomic.value == test }

        aktor.halt()
    }

    @Test
    fun shouldBeNotifiedWhenAnActorDoesNotExist() {
        val aktor = Aktor.new("site")
        val deadLetter = DeadLetter from aktor
        val directory = Directory from aktor

        val deadLetterAtomic: AtomicRef<ActorReference<*>?> = atomic(null)
        deadLetter configure { reference, _ -> deadLetterAtomic.getAndSet(reference) }

        val test = aktor actorFor TestActor.receiver

        directory register test

        val directoryAtomic = atomic(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { directoryAtomic.getAndSet(it.isNotEmpty()) })
        Await().atMost(5000).until { directoryAtomic.value }
        directoryAtomic.getAndSet(false)

        test tell Core.Kill

        directory find (aktor actorFor searchByType<TestActor.Protocol> { directoryAtomic.getAndSet(it.isNullOrEmpty()) })
        Await().atMost(5000).until { directoryAtomic.value }

        test tell TestActor.Dummy

        Await() atMost 5000 until { deadLetterAtomic.value == test }

        aktor.halt()
    }
}
