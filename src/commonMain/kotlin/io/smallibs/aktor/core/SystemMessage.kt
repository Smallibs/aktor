package io.smallibs.aktor.core

import io.smallibs.aktor.ActorReference

interface SystemMessage

object StopActor : SystemMessage
data class StoppedActor(val reference: ActorReference<*>) : SystemMessage

