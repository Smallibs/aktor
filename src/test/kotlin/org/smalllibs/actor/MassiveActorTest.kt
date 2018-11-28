package org.smalllibs.actor


import org.awaitility.Awaitility.await
import org.awaitility.Duration.FIVE_SECONDS
import org.junit.Test
import org.smalllibs.actor.engine.CoroutineBasedRunner
import org.smalllibs.actor.engine.ThreadBasedRunner
import java.util.concurrent.atomic.AtomicInteger

class MassiveActorTest {

    @Test
    fun shouldDoOneMillionTellsUsingThreads() {
        val system = ActorSystem.system("test", execution = ThreadBasedRunner())

        val called = AtomicInteger(0)

        val ACTORS = 1000
        val references = arrayOfNulls<ActorReference<Boolean>>(ACTORS)
        for (i in 0 until ACTORS) {
            references[i] = system.actorFor { _, _ -> called.incrementAndGet() }
        }

        var t0 = System.currentTimeMillis()

        val MESSAGES = 1000
        for (i in 0 until MESSAGES) {
            for (j in 0 until ACTORS) {
                references[j]?.let { it tell true }
            }
        }

        println("Submission done in " + (System.currentTimeMillis() - t0) + " ms")

        t0 = System.currentTimeMillis()

        await().atMost(FIVE_SECONDS).until { called.get() == MESSAGES * ACTORS }

        println("Execution of " + called.get() + " messages done in " + (System.currentTimeMillis() - t0) + " ms")
    }

    @Test
    fun shouldDoOneMillionTellsUsingCoroutine() {
        val system = ActorSystem.system("test", execution = CoroutineBasedRunner())

        val called = AtomicInteger(0)

        val ACTORS = 1000
        val references = arrayOfNulls<ActorReference<Boolean>>(ACTORS)
        for (i in 0 until ACTORS) {
            references[i] = system.actorFor { _, _ -> called.incrementAndGet() }
        }

        var t0 = System.currentTimeMillis()

        val MESSAGES = 1000
        for (i in 0 until MESSAGES) {
            for (j in 0 until ACTORS) {
                references[j]?.let { it tell true }
            }
        }

        println("Submission done in " + (System.currentTimeMillis() - t0) + " ms")

        t0 = System.currentTimeMillis()

        await().atMost(FIVE_SECONDS).until { called.get() == MESSAGES * ACTORS }

        println("Execution of " + called.get() + " messages done in " + (System.currentTimeMillis() - t0) + " ms")
    }

}