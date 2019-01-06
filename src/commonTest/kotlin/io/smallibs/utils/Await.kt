package io.smallibs.utils

// Should use Data

expect fun getCurrentTime(): Long;
expect fun waitFor(duration: Int);

object TimeOutException : Exception()

class Await(val timeout: Long) {
    fun until(predicate: () -> Boolean) {
        val currentTime = getCurrentTime()

        while (!predicate()) {
            if (getCurrentTime() - currentTime > timeout) {
                throw TimeOutException
            }

            waitFor(100)
        }
    }
}