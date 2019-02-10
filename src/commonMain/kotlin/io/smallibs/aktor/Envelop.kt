package io.smallibs.aktor

import io.smallibs.aktor.core.System

sealed class Envelop<T>
data class ProtocolEnvelop<T>(val content: T) : Envelop<T>()
data class SystemEnvelop<T>(val content: System.Protocol) : Envelop<T>()
