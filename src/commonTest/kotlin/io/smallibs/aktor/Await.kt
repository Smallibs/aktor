package io.smallibs.aktor

object Await {
    object Until {
        operator fun invoke(timeOut: Long, predicate: () -> Boolean): Boolean {
            var loop = 0

            while (!predicate()) {
                loop++

                if (loop > timeOut * 1000) {
                    return false;
                }
            }

            return true
        }
    }
}