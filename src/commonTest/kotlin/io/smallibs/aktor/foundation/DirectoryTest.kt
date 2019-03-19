package io.smallibs.aktor.foundation

import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.foundation.Directory.tryFound
import io.smallibs.utils.Await
import io.smallibs.utils.TimeOutException
import io.smallibs.utils.sleep
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DirectoryTest {

    object TestActor {
        interface Protocol

        val receiver: ProtocolBehavior<Protocol> = { a, _ -> a.behavior() }
    }

    @Test
    fun shouldRetrieveARegisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register (site actorFor TestActor.receiver)

        val atomic = atomic(false)
        directory find (site actorFor tryFound<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldRetrieveARegisteredActorUsingAlsoItsName() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register site.actorFor(TestActor.receiver, "test")

        val atomic = atomic(false)
        directory.find("test", site actorFor tryFound<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveARegisteredActorUsingAnotherName() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register (site actorFor TestActor.receiver)

        val atomic = atomic(false)
        directory.find("dummy", site actorFor tryFound<TestActor.Protocol>({}, { atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldUnregisterWhenKillingRegisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        val test = site actorFor TestActor.receiver

        directory register test

        val atomic = atomic(false)
        directory find (site actorFor tryFound<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }

        test tell Core.Kill

        atomic.getAndSet(false)
        directory find (site actorFor tryFound<TestActor.Protocol>({}, { atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveAnUnregisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        val atomic = atomic(false)
        directory find (site actorFor tryFound<Directory.Protocol>({ atomic.getAndSet(true) }))

        assertFailsWith<TimeOutException> { Await(5000).until { atomic.value } }
    }
}