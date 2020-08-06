package io.smallibs.aktor.foundation

import io.smallibs.aktor.ActorReference

class Stashed<T> private constructor(private var actions: List<T> = listOf(), private val capacity: Int) {

    constructor(capacity: Int = Int.MAX_VALUE) : this(listOf(), capacity)

    fun stash(action: T): Boolean {
        val canStash = actions.size < capacity

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
