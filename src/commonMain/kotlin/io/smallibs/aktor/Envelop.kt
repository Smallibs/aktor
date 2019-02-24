package io.smallibs.aktor

import io.smallibs.aktor.core.Core

sealed class Envelop<T>
data class CoreEnvelop<T>(val content: Core.Protocol) : Envelop<T>()
data class ProtocolEnvelop<T>(val content: T) : Envelop<T>()
