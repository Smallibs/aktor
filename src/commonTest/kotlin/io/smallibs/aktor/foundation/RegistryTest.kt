package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.ActorSystem
import io.smallibs.aktor.Receiver
import io.smallibs.utils.Await
import io.smallibs.utils.TimeOutException
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RegistryTest {

    @Test
    fun shouldRegisterAndRetrieveActor() {
        val system = ActorSystem.system("test")

        val registry = system actorFor Registry.new()
        registry tell Registry.RegisterActor(Registry.RegistryMessage::class, registry)

        fun <T : Any> search(
            success: (ActorReference<T>) -> Unit,
            failure: () -> Unit = {}
        ): Receiver<Registry.SearchActorResponse<T>> = { _, envelop ->
            envelop.content.reference?.let { success(it) } ?: failure()
        }

        // oO -- remove the 'as'
        val atomic = atomic(false)
        val search = (system actorFor search<Registry>({ atomic.getAndSet(true )})) as ActorReference<Registry.SearchActorResponse<*>>

        registry tell Registry.SearchActor(Registry.RegistryMessage::class, search)

        Await(5000).until { atomic.value }
    }

    @Test
    fun shouldNotRetrieveActor() {
        val system = ActorSystem.system("test")

        val registry = system actorFor Registry.new()

        fun <T : Any> search(
            success: (ActorReference<T>) -> Unit,
            failure: () -> Unit = {}
        ): Receiver<Registry.SearchActorResponse<T>> = { _, envelop ->
            envelop.content.reference?.let { success(it) } ?: failure()
        }

        // oO -- remove the 'as'
        val atomic = atomic(false)
        val search = (system actorFor search<Registry>({ atomic.getAndSet(true )})) as ActorReference<Registry.SearchActorResponse<*>>

        registry tell Registry.SearchActor(Registry.RegistryMessage::class, search)

        assertFailsWith<TimeOutException> { Await(5000).until { atomic.value } }
    }
}