package io.smallibs.aktor

import org.awaitility.Duration
import org.awaitility.kotlin.await
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class PingPongTest {

    class PingPong(val sender: ActorReference<PingPong>)

    private fun arbiter(nbEnded: AtomicInteger): Receiver<String> = { _, message ->
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

        val endedPlayers = AtomicInteger()

        val arbiter = system.actorFor(arbiter(endedPlayers))
        val ping = system.actorFor(player(arbiter, "ping"))
        val pong = system.actorFor(player(arbiter, "pong"))

        ping tell PingPong(pong)

        await.atMost(Duration.FIVE_SECONDS).until {
            endedPlayers.get() == 1
        }
    }
}
