package io.smallibs.aktor.system

import io.smallibs.aktor.ActorReference

interface SystemMessage

data class ActorStopped(val reference: ActorReference<*>) : SystemMessage
