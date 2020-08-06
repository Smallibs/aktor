package io.smallibs.utils

// Should use Data

expect fun currentTimeMillis(): Long
expect fun sleep(duration: Int)

object TimeOutException : Exception()

class Await private constructor(private val timeout: Long, private val sleepDuration: Int) {

    constructor() : this(500, 100)

    infix fun atMost(timeout: Long): Await = Await(timeout, sleepDuration)

    infix fun every(delay: Int): Await = Await(timeout, delay)

    infix fun until(predicate: () -> Boolean) {
        val currentTime = currentTimeMillis()

        while (!predicate()) {
            if (currentTimeMillis() - currentTime > timeout) {
                throw TimeOutException
            }

            sleep(sleepDuration)
        }
    }
}