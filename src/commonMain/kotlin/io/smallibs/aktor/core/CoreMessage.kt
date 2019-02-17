package io.smallibs.aktor.core

import io.smallibs.aktor.ActorReference

interface Core {

    interface Protocol
    object Start : Protocol
    object Stop : Protocol
    data class Stopped(val reference: ActorReference<*>) : Protocol

}

