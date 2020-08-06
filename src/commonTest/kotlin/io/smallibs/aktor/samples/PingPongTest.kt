package io.smallibs.aktor.samples

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class PingPongTest {

    class PingPong(val sender: ActorReference<PingPong>)

    private fun arbiter(endedGame: AtomicBoolean): ProtocolBehavior<String> = { actor, message ->
        println("${message.content} ending game ...")
        endedGame.getAndSet(true)
        actor.same()
    }

    private fun player(arbiter: ActorReference<String>, name: String, turn: Int = 0): ProtocolBehavior<PingPong> =
        { actor, message ->
            if (turn < 1_000) {
                message.content.sender tell PingPong(actor.context.self)
                actor become player(arbiter, name, turn + 1)
            } else {
                arbiter tell name
                actor become endGame
            }
        }

    private val endGame: ProtocolBehavior<PingPong> = { a, _ -> a.same() }

    @Test
    fun shouldPlayGame() {
        val aktor = Aktor.new("test")

        val endedGame = atomic(false)

        val arbiter = aktor.actorFor(arbiter(endedGame))
        val ping = aktor.actorFor(player(arbiter, "ping"))
        val pong = aktor.actorFor(player(arbiter, "pong"))

        ping tell PingPong(pong)

        Await() atMost 5000 until { endedGame.value }

        aktor.halt()
    }
}
