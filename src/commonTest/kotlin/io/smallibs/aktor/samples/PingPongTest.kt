package io.smallibs.aktor.samples

import io.smallibs.aktor.ActorReference
import io.smallibs.aktor.Aktor
import io.smallibs.aktor.ProtocolReceiver
import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class PingPongTest {

    class PingPong(val sender: ActorReference<PingPong>)

    private fun arbiter(nbEnded: AtomicInt): ProtocolReceiver<String> = { _, message ->
        println("${message.content} ending game ...")
        nbEnded.incrementAndGet()
    }

    private fun player(arbiter: ActorReference<String>, name: String, turn: Int = 0): ProtocolReceiver<PingPong> =
        { actor, message ->
            if (turn < 1_000) {
                actor become player(arbiter, name, turn + 1)
                message.content.sender tell PingPong(actor.context.self)
            } else {
                actor become endGame
                arbiter tell name
            }
        }

    private val endGame: ProtocolReceiver<PingPong> = { _, _ -> }

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