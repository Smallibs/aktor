package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.core.Core
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

        val receiver: ProtocolReceiver<Protocol> = { _, _ -> reject.exhaustive }
    }

    @Test
    fun shouldBeNotifiedWhenAnActorDoesNotManageAndMessage() {
        val site = Aktor.new("site")
        val deadLetter = DeadLetter from site.system

        val test = site actorFor TestActor.receiver

        val atomic: AtomicRef<ActorReference<*>?> = atomic(null)
        deadLetter configure { reference, _ -> atomic.getAndSet(reference) }

        test tell TestActor.Dummy

        Await(5000).until { atomic.value == test }
    }

    @Test
    fun shouldBeNotifiedWhenAnActorDoesNotExist() {
        val site = Aktor.new("site")
        val deadLetter = DeadLetter from site.system
        val directory = Directory from site.system

        val test = site actorFor TestActor.receiver

        directory register test

        val directoryAtomic = atomic(false)
        directory find (site actorFor Directory.onSearchComplete<TestActor.Protocol>({
            directoryAtomic.getAndSet(
                true
            )
        }))
        Await(5000).until { directoryAtomic.value }

        test tell Core.Kill

        directoryAtomic.getAndSet(false)
        directory find (site actorFor Directory.onSearchComplete<TestActor.Protocol>(
            {},
            { directoryAtomic.getAndSet(true) }))
        Await(5000).until { directoryAtomic.value }

        val deadLetterAtomic: AtomicRef<ActorReference<*>?> = atomic(null)
        deadLetter configure { reference, _ -> deadLetterAtomic.getAndSet(reference) }

        test tell TestActor.Dummy

        Await(5000).until { deadLetterAtomic.value == test }
    }
}