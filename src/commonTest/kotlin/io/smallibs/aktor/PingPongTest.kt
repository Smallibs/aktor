package io.smallibs.aktor

import io.smallibs.utils.Await
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.test.assertTrue

class PingPongTest {

    class PingPong(val sender: ActorReference<PingPong>)

    private fun arbiter(nbEnded: AtomicInt): Receiver<String> = { _, message ->
        println("${message.content} ending game ...")
        nbEnded.incrementAndGet()
    }

    private fun player(arbiter: ActorReference<String>, name: String, turn: Int = 0): Receiver<PingPong> =
        { actor, message ->
            if (turn < 1_000) {
                actor start player(arbiter, name, turn + 1)
                message.content.sender tell PingPong(actor.context.self)
            } else {
                actor start endGame
                arbiter tell name
            }
        }

    private val endGame: Receiver<PingPong> = { _, _ ->
        // Do nothing
    }

    @Test
    fun shouldPlayGame() {
        val system = ActorSystem.system("test")

        val endedPlayers = atomic(0)

        val arbiter = system.actorFor(arbiter(endedPlayers))
        val ping = system.actorFor(player(arbiter, "ping"))
        val pong = system.actorFor(player(arbiter, "pong"))

        ping tell PingPong(pong)


        assertTrue { Await.Until(5000) { endedPlayers.value == 1 } }

    }

}