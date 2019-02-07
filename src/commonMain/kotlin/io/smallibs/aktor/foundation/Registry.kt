package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Receiver
import kotlin.reflect.KClass

class Registry {

    interface Protocol
    data class RegisterActor<T : Any>(val type: KClass<T>, val reference: ActorReference<T>) : Protocol
    data class UnregisterActor(val type: KClass<*>) : Protocol
    data class SearchActor(val type: KClass<*>, val sender: ActorReference<SearchActorResponse<*>>) : Protocol

    data class SearchActorResponse<T>(val reference: ActorReference<T>?)

    //
    // registry behavior
    //

    fun registry(actors: Map<KClass<*>, ActorReference<*>>): Receiver<Protocol> =
        { actor, message ->
            val content = message.content

            when (content) {
                is RegisterActor<*> ->
                    actor become registry(actors + Pair(content.type, content.reference))
                is UnregisterActor ->
                    actor become registry(actors.filter { entry -> entry.key == content.type })
                is SearchActor ->
                    content.sender tell SearchActorResponse(actors[content.type])
            }
        }

    companion object {
        fun new(): Behavior<Protocol> = Behavior of Registry().registry(mapOf())

        inline fun <reified T : Any> register(reference: ActorReference<T>): RegisterActor<T> =
            RegisterActor(T::class, reference)

        inline fun <reified T : Any> findActor(receptor: ActorReference<SearchActorResponse<T>>): SearchActor =
            SearchActor(T::class, receptor as ActorReference<SearchActorResponse<*>>)

        inline fun <reified T : Any> unregister(): UnregisterActor =
            UnregisterActor(T::class)
    }

}