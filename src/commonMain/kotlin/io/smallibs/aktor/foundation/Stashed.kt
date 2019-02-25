package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference

class Stashed<T>(var actions: List<T>, val capacity: Int = Int.MAX_VALUE) {

    fun stash(action: T) {
        actions = actions + action
    }

    fun unstashAll(a: ActorReference<T>) {
        actions.forEach { a tell it }
        actions = listOf()
    }

}