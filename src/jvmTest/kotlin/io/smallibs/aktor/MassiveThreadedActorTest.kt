package io.smallibs.aktor


import io.smallibs.aktor.Aktor.new
import io.smallibs.aktor.runner.ThreadBasedRunner
import io.smallibs.utils.Await
import kotlinx.atomicfu.atomic
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.text.DecimalFormat
import java.time.Duration

private const val actors = 1000
private const val messages = 1000

class MassiveThreadedActorTest {

    private val format = DecimalFormat.getIntegerInstance()
    private fun Int.format() = format.format(this)

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

        val called = atomic(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { a, _ -> called.incrementAndGet(); a.same() }
        }

        stopWatch({ "Execution of ${called.value.format()} messages using Threads" }) {
            stopWatch({ "Submission" }) {
                repeat(messages) {
                    references.forEach { a -> a tell true }
                }
            }

            Await(FIVE_SECONDS.valueInMS).until {
                called.value == messages * actors
            }
        }

    }

}