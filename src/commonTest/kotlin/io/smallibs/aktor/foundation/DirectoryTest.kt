package io.smallibs.aktor.foundation

import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.foundation.Directory.onSearchComplete
import io.smallibs.utils.Await
import io.smallibs.utils.TimeOutException
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DirectoryTest {

    object TestActor {
        interface Protocol

        val receiver: ProtocolReceiver<Protocol> = { _, _ -> }
    }

    @Test
    fun shouldRetrieveARegisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register (site.actorFor(TestActor.receiver, "test"))

        val atomic = atomic(false)
        directory find (site actorFor onSearchComplete<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldRetrieveARegisteredActorUsingAlsoItsName() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register (site.actorFor(TestActor.receiver, "test"))

        val atomic = atomic(false)
        directory.find("test", site actorFor onSearchComplete<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveARegisteredActorUsingAnotherName() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        directory register (site.actorFor(TestActor.receiver, "test"))

        val atomic = atomic(false)
        directory.find("dummy", site actorFor onSearchComplete<TestActor.Protocol>({}, { atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldUnregisterWhenStoppingRegisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        val test = site.actorFor(TestActor.receiver, "test")

        directory register test

        val atomic = atomic(false)
        directory find (site actorFor onSearchComplete<TestActor.Protocol>({ atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }

        test tell Core.Kill

        atomic.getAndSet(false)
        directory find (site actorFor onSearchComplete<TestActor.Protocol>({}, { atomic.getAndSet(true) }))

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveAnUnregisteredActor() {
        val site = Aktor.new("site")
        val directory = Directory from site.system

        val atomic = atomic(false)
        directory find (site actorFor onSearchComplete<Directory.Protocol>({ atomic.getAndSet(true) }))

        assertFailsWith<TimeOutException> { Await(5000).until { atomic.value } }
    }
}