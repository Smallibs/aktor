package io.smallibs.utils

// Should use Data

expect fun getCurrentTime(): Long;
expect fun waitFor(duration: Int);

object Await {
    object Until {
        operator fun invoke(timeOut: Long, predicate: () -> Boolean): Boolean {
            val currentTime = getCurrentTime()

            while (!predicate()) {
                if (getCurrentTime() - currentTime > timeOut) {
                    return false
                }

                waitFor(100)
            }

            return true
        }
    }
}