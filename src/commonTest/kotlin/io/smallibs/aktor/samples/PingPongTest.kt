package io.smallibs.aktor.samples

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.Behavior
import io.smallibs.aktor.ProtocolBehavior
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class PingPongTest {

    class PingPong(val sender: ActorReference<PingPong>)

    private fun arbiter(nbEnded: AtomicInt): ProtocolBehavior<String> = { actor, message ->
        println("${message.content} ending game ...")
        nbEnded.incrementAndGet()
        actor.behavior()
    }

    private fun player(arbiter: ActorReference<String>, name: String, turn: Int = 0): ProtocolBehavior<PingPong> =
        { actor, message ->
            if (turn < 1_000) {
                message.content.sender tell PingPong(actor.context.self)
                Behavior of player(arbiter, name, turn + 1)
            } else {
                arbiter tell name
                Behavior of endGame
            }
        }

    private val endGame: ProtocolBehavior<PingPong> = { a, _ -> a.behavior() }

    @Test
    fun shouldPlayGame() {
        val system = Aktor.new("test")

        val endedPlayers = atomic(0)

        val arbiter = system.actorFor(arbiter(endedPlayers))
        val ping = system.actorFor(player(arbiter, "ping"))
        val pong = system.actorFor(player(arbiter, "pong"))

        ping tell PingPong(pong)

        Await(5000).until { endedPlayers.value == 1 }
    }

}