package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject
import kotlin.reflect.KClass

object Directory {

    interface Protocol
    data class RegisterActor<T : Any>(val type: KClass<T>, val reference: ActorReference<T>) : Protocol
    data class UnregisterActor(val reference: ActorReference<*>) : Protocol
    data class SearchActor(val type: KClass<*>, val sender: ActorReference<SearchActorResponse<*>>) : Protocol
    data class SearchActorResponse<T>(val reference: ActorReference<T>?)

    private fun registry(actors: Map<KClass<*>, ActorReference<*>>): ProtocolReceiver<Protocol> =
        { actor, message ->
            val content = message.content

            when (content) {
                is RegisterActor<*> ->
                    actor become registry(actors + Pair(content.type, content.reference))
                is UnregisterActor ->
                    actor become registry(actors.filter { entry -> entry.value.address != content.reference.address })
                is SearchActor ->
                    content.sender tell SearchActorResponse(actors[content.type])
                else ->
                    reject
            }.exhaustive
        }

    fun new(): Behavior<Protocol> = Behavior of Directory.registry(mapOf())

    infix fun from(system: ActorReference<System.Protocol>): Bridge = Bridge { message ->
        system tell System.ToDirectory(message)
    }

    class Bridge(val bridge: (Protocol) -> Unit) {
        inline infix fun <reified T : Any> register(reference: ActorReference<T>) =
            bridge(RegisterActor(T::class, reference))

        @Suppress("UNCHECKED_CAST")
        inline infix fun <reified T : Any> find(receptor: ActorReference<SearchActorResponse<T>>) =
            bridge(SearchActor(T::class, receptor as ActorReference<SearchActorResponse<*>>))

        infix fun unregister(reference: ActorReference<*>) =
            bridge(UnregisterActor(reference))

    }

    fun <T : Any> onSearchComplete(
        success: (ActorReference<T>) -> Unit,
        failure: () -> Unit = { }
    ): ProtocolReceiver<Directory.SearchActorResponse<T>> = { _, envelop ->
        envelop.content.reference?.let { success(it) } ?: failure()
    }

}
