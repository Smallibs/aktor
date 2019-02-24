package io.smallibs.utils

// Should use Data

expect fun currentTimeMillis(): Long
expect fun sleep(duration: Int)

object TimeOutException : Exception()

class Await(val timeout: Long) {
    fun until(predicate: () -> Boolean) {
        val currentTime = currentTimeMillis()

        while (!predicate()) {
            if (currentTimeMillis() - currentTime > timeout) {
                throw TimeOutException
            }

            sleep(100)
        }
    }
}