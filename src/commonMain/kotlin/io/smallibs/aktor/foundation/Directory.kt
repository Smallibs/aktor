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

    //
    // registry exhaustive
    //

    private fun registry(actors: Map<KClass<*>, ActorReference<*>>): ProtocolReceiver<Protocol> =
        { actor, message ->
            val content = message.content

            when (content) {
                is RegisterActor<*> ->
                    actor become registry(actors + Pair(content.type, content.reference))
                is UnregisterActor ->
                    actor become registry(actors.filter { entry -> entry.value.address == content.reference.address })
                is SearchActor ->
                    content.sender tell SearchActorResponse(actors[content.type])
                else -> reject
            }.exhaustive
        }

    fun new(): Behavior<Protocol> = Behavior of Directory.registry(mapOf())

    inline fun <reified T : Any> register(reference: ActorReference<T>): RegisterActor<T> =
        RegisterActor(T::class, reference)

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> findActor(receptor: ActorReference<SearchActorResponse<T>>): SearchActor =
        SearchActor(T::class, receptor as ActorReference<SearchActorResponse<*>>)

    fun unregister(reference: ActorReference<*>): UnregisterActor =
        UnregisterActor(reference)

    fun with(system: ActorReference<System.Protocol>) = { message: Protocol ->
        system tell System.ToDirectory(message)
    }

}