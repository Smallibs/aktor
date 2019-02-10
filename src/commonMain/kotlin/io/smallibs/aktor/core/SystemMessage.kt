package io.smallibs.aktor.core

import io.smallibs.aktor.ActorReference

interface System {

    interface Protocol
    object StopActor : Protocol
    data class StoppedActor(val reference: ActorReference<*>) : Protocol

}

