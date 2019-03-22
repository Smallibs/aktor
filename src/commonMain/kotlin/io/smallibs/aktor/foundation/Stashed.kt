package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference

class Stashed<T>(var actions: List<T>, private val capacity: Int = Int.MAX_VALUE) {

    fun stash(action: T): Boolean {
        val canStash  = actions.size < capacity

        if (canStash) {
            actions = actions + action
        }

        return canStash
    }

    fun unStashAll(a: ActorReference<T>) {
        actions.forEach { a tell it }
        actions = listOf()
    }

}