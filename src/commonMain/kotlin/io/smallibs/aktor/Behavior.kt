@file:Suppress("UNUSED_PARAMETER")

package io.smallibs.aktor

import io.smallibs.aktor.core.Core.Behaviors
import io.smallibs.aktor.utils.Exhaustive

typealias CoreBehavior<T> = (Actor<T>, CoreEnvelop<T>) -> Behavior<T>
typealias ProtocolBehavior<T> = (Actor<T>, ProtocolEnvelop<T>) -> Behavior<T>
typealias ExhaustiveProtocolBehavior<T> = (Actor<T>, ProtocolEnvelop<T>) -> Exhaustive<Behavior<T>>

interface Behavior<T> {

    val core: CoreBehavior<T>
    val protocol: ProtocolBehavior<T>

    fun receive(actor: Actor<T>, envelop: Envelop<T>): Behavior<T> =
        when (envelop) {
            is CoreEnvelop<T> -> core(actor, envelop)
            is ProtocolEnvelop<T> -> protocol(actor, envelop)
        }

    fun onStart(actor: Actor<T>) {}

    fun onStop(actor: Actor<T>) {}

    fun onKill(actor: Actor<T>) {}

    companion object {
        infix fun <T> of(protocol: ProtocolBehavior<T>): Behavior<T> =
            ForReceiver(Behaviors.core(), protocol)

        infix fun <T> of(receivers: Pair<CoreBehavior<T>, ProtocolBehavior<T>>): Behavior<T> =
            ForReceiver(receivers.first, receivers.second)

        class ForReceiver<T>(
            override val core: CoreBehavior<T>,
            override val protocol: ProtocolBehavior<T>
        ) : Behavior<T>
    }
}
