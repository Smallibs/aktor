package io.smallibs.aktor


import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import java.text.DecimalFormat
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger


private const val actors = 1000
private const val messages = 1000

class MassiveActorTest {


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
        val system = ActorSystem.system("test", execution = ActorRunner.threaded())

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

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val system = ActorSystem.system("test", execution = ActorRunner.coroutine())

        val called = AtomicInteger(0)

        val references = (0 until actors).map {
            system.actorFor<Boolean> { _, _ -> called.incrementAndGet() }
        }

        stopWatch({ "Submission" }) {
            repeat(messages) {
                references.forEach { a -> a tell true }
            }
        }

        stopWatch({ "Execution of ${called.get().format()} messages using Coroutine" }) {
            await().atMost(FIVE_SECONDS).until {
                called.get() == messages * actors
            }
        }
    }

}