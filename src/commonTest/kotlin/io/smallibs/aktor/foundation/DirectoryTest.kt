package io.smallibs.aktor.foundation

import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.foundation.Directory.searchByName
import io.smallibs.aktor.foundation.Directory.searchByType
import io.smallibs.utils.Await
import io.smallibs.utils.TimeOutException
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DirectoryTest {

    object TestActor {
        interface Protocol

        val receiver: ProtocolBehavior<Protocol> = { a, _ -> a.same() }
    }

    @Test
    fun shouldRetrieveARegisteredActor() {
        val aktor = Aktor.new("site")
        val directory = Directory from aktor

        directory register (aktor actorFor TestActor.receiver)

        val atomic = atomic(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { atomic.getAndSet(true) })

        Await().atMost(5000).until { atomic.value }

        aktor.halt()
    }

    @Test
    fun shouldRetrieveARegisteredActorUsingAlsoItsName() {
        val aktor = Aktor.new("site")
        val directory = Directory from aktor

        directory register aktor.actorFor(TestActor.receiver, "test")

        val atomic = atomic(false)
        directory.find("test", aktor actorFor searchByName<TestActor.Protocol> { atomic.getAndSet(it != null) })

        Await().atMost(5000).until { atomic.value }

        aktor.halt()
    }

    @Test
    fun shouldNotRetrieveARegisteredActorUsingAWrongName() {
        val aktor = Aktor.new("site")
        val directory = Directory from aktor

        directory register (aktor actorFor TestActor.receiver)

        val atomic = atomic(false)
        directory.find("dummy", aktor actorFor searchByName<TestActor.Protocol> { atomic.getAndSet(it == null) })

        Await().atMost(5000).until { atomic.value }

        aktor.halt()
    }

    @Test
    fun shouldUnregisterWhenKillingRegisteredActor() {
        val aktor = Aktor.new("site")
        val directory = Directory from aktor

        val test = aktor actorFor TestActor.receiver

        directory register test

        val atomic = atomic(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { atomic.getAndSet(it.isNotEmpty()) })

        Await().atMost(5000).until { atomic.value }

        test tell Core.Kill

        atomic.getAndSet(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { atomic.getAndSet(it.isNullOrEmpty()) })

        Await().atMost(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveAnUnregisteredActor() {
        val aktor = Aktor.new("site")

        val directory = Directory from aktor

        val atomic = atomic(false)
        directory find (aktor actorFor searchByType<Directory.Protocol> { atomic.getAndSet(it.isNotEmpty()) })

        assertFailsWith<TimeOutException> { Await().atMost(5000).until { atomic.value } }

        aktor.halt()
    }

    @Test
    fun shouldNotRetrieveAnRegisteredAndThenUnregisteredActor() {
        val aktor = Aktor.new("site")
        val directory = Directory from aktor

        val test = aktor actorFor TestActor.receiver

        directory register test
        val atomic = atomic(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { atomic.getAndSet(it.isNotEmpty()) })

        Await() atMost 5000 until { atomic.value }

        directory unregister test

        atomic.getAndSet(false)
        directory find (aktor actorFor searchByType<TestActor.Protocol> { atomic.getAndSet(it.isNullOrEmpty()) })

        Await() atMost 5000 until { atomic.value }

        aktor.halt()
    }
}