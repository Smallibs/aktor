@file:Suppress("UNUSED_PARAMETER")

package io.smallibs.aktor

import io.smallibs.aktor.core.Core.Behaviors
import io.smallibs.aktor.utils.Exhaustive

typealias CoreReceiver<T> = (Actor<T>, CoreEnvelop<T>) -> Unit
typealias ProtocolReceiver<T> = (Actor<T>, ProtocolEnvelop<T>) -> Unit
typealias ExhaustiveProtocolReceiver<T> = (Actor<T>, ProtocolEnvelop<T>) -> Exhaustive<Unit>

interface Behavior<T> {

    val core: CoreReceiver<T>
    val protocol: ProtocolReceiver<T>

    fun receive(actor: Actor<T>, envelop: Envelop<T>): Unit =
        when (envelop) {
            is CoreEnvelop<T> -> core(actor, envelop)
            is ProtocolEnvelop<T> -> protocol(actor, envelop)
        }

    fun onStart(actor: Actor<T>) {}

    fun onResume(actor: Actor<T>) {}

    fun onPause(actor: Actor<T>) {}

    fun onFinish(actor: Actor<T>) {}

    companion object {
        infix fun <T> of(protocol: ProtocolReceiver<T>): Behavior<T> =
            ForReceiver(Behaviors.core, protocol)

        infix fun <T> of(receivers: Pair<CoreReceiver<T>, ProtocolReceiver<T>>): Behavior<T> =
            ForReceiver(receivers.first, receivers.second)

        class ForReceiver<T>(
            override val core: CoreReceiver<T>,
            override val protocol: ProtocolReceiver<T>
        ) : Behavior<T>
    }
}