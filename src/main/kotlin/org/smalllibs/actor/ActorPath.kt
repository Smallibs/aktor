package org.smalllibs.actor

import java.util.*

interface ActorPath {

    fun name(): String

    fun parent(): ActorPath?

    companion object {
        fun freshName(): String {
            return UUID.randomUUID().toString()
        }
    }

}
