package io.smallibs.aktor


import io.smallibs.aktor.ActorSystem.Companion.new
import io.smallibs.aktor.runner.ThreadBasedRunner
import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.text.DecimalFormat
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

private const val actors = 1000
private const val messages = 1000

class MassiveThreadedActorTest {

    private val format = DecimalFormat.getIntegerInstance()
    private fun Int.format(): String =
        format.format(this)

    private inline fun <T> stopWatch(label: () -> String, block: () -> T): T {
        val start = System.nanoTime()
        val result = block()
        val duration = Duration.ofNanos(System.nanoTime() - start)
        println("${label()} done in ${duration.toMillis()} ms")
        return result
    }

    @Test
    fun shouldDoOneMillionTellsUsingThreads() {
        val system = new("test", execution = ThreadBasedRunner())

        val called = AtomicInteger(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { _, _ -> called.incrementAndGet() }
        }

        stopWatch({ "Submission" }) {
            repeat(messages) {
                references.forEach { a -> a tell true }
            }
        }

        stopWatch({ "Execution of ${called.get().format()} messages using Threads" }) {
            await().atMost(FIVE_SECONDS).until {
                called.get() == messages * actors
            }
        }

    }

}