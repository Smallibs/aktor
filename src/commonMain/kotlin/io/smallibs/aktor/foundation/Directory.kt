package io.smallibs.aktor.foundation

import io.smallibs.aktor.Actor
import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.aktor.core.Core
import io.smallibs.aktor.utils.exhaustive
import io.smallibs.aktor.utils.reject
import kotlin.reflect.KClass

object Directory {

    const val name = "directory"

    interface Protocol

    data class RegisterActor<T : Any>(
        val type: KClass<T>,
        val reference: ActorReference<T>
    ) : Protocol

    data class UnregisterActor(
        val reference: ActorReference<*>
    ) : Protocol

    data class SearchActorsByType(
        val type: KClass<*>,
        val sender: ActorReference<SearchActorsResponse<*>>
    ) : Protocol

    data class SearchActorByNameAndType(
        val type: KClass<*>,
        val name: String,
        val sender: ActorReference<SearchActorResponse<*>>
    ) : Protocol

    data class SearchActorsResponse<T>(
        val references: List<ActorReference<T>>
    )

    data class SearchActorResponse<T>(
        val reference: ActorReference<T>?
    )

    @Suppress("UNCHECKED_CAST")
    private fun registry(actors: List<Pair<KClass<*>, ActorReference<*>>>): ProtocolBehavior<Protocol> =
        { actor, message ->
            val content = message.content

            when (content) {
                is RegisterActor<*> ->
                    actor become registry(actors + Pair(content.type, content.reference))
                is UnregisterActor ->
                    actor become registry(actors.filter { entry -> entry.second.address != content.reference.address })
                is SearchActorsByType -> {
                    val foundActors = actors
                        .filter { entry -> entry.first == content.type }
                        .map { entry -> entry.second }

                    content.sender tell SearchActorsResponse(foundActors as List<ActorReference<Any?>>)
                    actor.same()
                }
                is SearchActorByNameAndType -> {
                    val foundActor = actors
                        .filter { entry -> entry.first == content.type }
                        .map { entry -> entry.second }
                        .find { reference -> content.name == reference.address.name }

                    content.sender tell SearchActorResponse(foundActor)
                    actor.same()
                }
                else ->
                    reject
            }.exhaustive.value
        }

    fun new(): Behavior<Protocol> = Behavior of registry(listOf())

    infix fun from(actor: Actor<*>): Bridge =
        Directory from actor.context.self

    infix fun from(reference: ActorReference<*>): Bridge = Bridge { message ->
        reference tell Core.ToRoot(System.ToDirectory(message))
    }

    class Bridge(val bridge: (Protocol) -> Unit) {
        inline infix fun <reified T : Any> register(reference: ActorReference<T>) =
            bridge(RegisterActor(T::class, reference))

        @Suppress("UNCHECKED_CAST")
        inline infix fun <reified T : Any> find(receptor: ActorReference<SearchActorsResponse<T>>) =
            bridge(SearchActorsByType(T::class, receptor as ActorReference<SearchActorsResponse<*>>))

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any> find(name: String, receptor: ActorReference<SearchActorResponse<T>>) =
            bridge(SearchActorByNameAndType(T::class, name, receptor as ActorReference<SearchActorResponse<*>>))

        infix fun unregister(reference: ActorReference<*>) =
            bridge(UnregisterActor(reference))

    }

    fun <T : Any> searchByType(
        success: (List<ActorReference<T>>) -> Unit
    ): ProtocolBehavior<SearchActorsResponse<T>> = { actor, envelop ->
        actor.context.self tell Core.Kill
        success(envelop.content.references)
        actor.same()
    }

    fun <T : Any> searchByName(
        success: (ActorReference<T>?) -> Unit
    ): ProtocolBehavior<SearchActorResponse<T>> = { actor, envelop ->
        actor.context.self tell Core.Kill
        success(envelop.content.reference)
        actor.same()
    }
}
