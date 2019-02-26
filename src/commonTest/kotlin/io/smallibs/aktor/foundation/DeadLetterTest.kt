package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.utils.NotExhaustive
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class DeadLetterTest {

    object TestActor {
        interface Protocol
        object Dummy : Protocol

        val receiver: ProtocolReceiver<Protocol> = { _, _ -> throw NotExhaustive() }
    }

    @Test
    fun shouldRetrieveARegisteredActor() {
        val site = Aktor.new("site")
        val directory = DeadLetter from site.system

        val testReference = site.actorFor(TestActor.receiver, "test")

        val atomic: AtomicRef<ActorReference<*>?> = atomic(null)
        directory configure { reference, _ -> atomic.getAndSet(reference) }

        testReference tell TestActor.Dummy

        Await(5000).until { atomic.value == testReference }
    }

}