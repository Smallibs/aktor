package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.Receiver
import kotlin.reflect.KClass

class Registry {

    interface RegistryMessage
    data class RegisterActor<T : Any>(val type: KClass<T>, val reference: ActorReference<T>) : RegistryMessage
    data class UnregisterActor(val reference: ActorReference<*>) : RegistryMessage
    data class SearchActor(val type: KClass<*>, val sender: ActorReference<SearchActorResponse<*>>) :
        RegistryMessage

    data class SearchActorResponse<T>(val reference: ActorReference<T>?)

    fun registry(actors: Map<KClass<*>, ActorReference<*>>): Receiver<RegistryMessage> =
        { actor, message ->
            val content = message.content

            when (content) {
                is RegisterActor<*> ->
                    actor start registry(actors + Pair(content.type, content.reference))
                is UnregisterActor ->
                    actor start registry(actors.filter { entry -> entry.value.address == content.reference.address })
                is SearchActor ->
                    content.sender tell SearchActorResponse(actors[content.type])
            }
        }

    companion object {
        fun new(): Behavior<RegistryMessage> = Behavior of Registry().registry(mapOf())
    }

}