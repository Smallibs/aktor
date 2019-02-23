package io.smallibs.aktor.foundation

import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolReceiver
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
    fun shouldRegisterAndRetrieveActor() {
        val site = Aktor.new("test")
        val directory = Directory from site.system

        directory register (site actorFor TestActor.receiver)

        val atomic = atomic(false)
        val receptor = site actorFor onSearchComplete<TestActor.Protocol>({ atomic.getAndSet(true) })

        directory find receptor

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveActor() {
        val site = Aktor.new("test")
        val directory = Directory from site.system

        val atomic = atomic(false)
        val receptor = site actorFor onSearchComplete<Directory.Protocol>({ atomic.getAndSet(true) })

        directory find receptor

        assertFailsWith<TimeOutException> { Await(5000).until { atomic.value } }
    }
}