package org.smalllibs.actor

import java.util.*

interface ActorPath {

    val name: String

    val parent: ActorPath?

    companion object {
        fun freshName(): String {
            return UUID.randomUUID().toString()
        }
    }

}
