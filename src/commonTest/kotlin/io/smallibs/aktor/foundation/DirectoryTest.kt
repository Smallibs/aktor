package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Bootstrap
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.utils.Await
import io.smallibs.utils.TimeOutException
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DirectoryTest {

    @Test
    fun shouldRegisterAndRetrieveActor() {
        val site = Bootstrap.new("test")

        val registry = site actorFor Directory.new()
        registry tell Directory.register(registry)

        fun <T : Any> waitingFor(
            success: (ActorReference<T>) -> Boolean,
            failure: () -> Boolean = { true }
        ): ProtocolReceiver<Directory.SearchActorResponse<T>> = { _, envelop ->
            envelop.content.reference?.let { success(it) } ?: failure()
        }

        val atomic = atomic(false)
        val receptor = site actorFor waitingFor<Directory.Protocol>({ atomic.getAndSet(true) })

        registry tell Directory.findActor(receptor)

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveActor() {
        val site = Bootstrap.new("test")

        val registry = site actorFor Directory.new()

        fun <T : Any> waitingFor(
            success: (ActorReference<T>) -> Boolean,
            failure: () -> Boolean = { true }
        ): ProtocolReceiver<Directory.SearchActorResponse<T>> = { _, envelop ->
            envelop.content.reference?.let { success(it) } ?: failure()
        }

        val atomic = atomic(false)
        val receptor = site actorFor waitingFor<Directory.Protocol>({ atomic.getAndSet(true) })

        registry tell Directory.findActor(receptor)

        assertFailsWith<TimeOutException> { Await(5000).until { atomic.value } }
    }
}