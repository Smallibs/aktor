package io.smallibs.utils

class Await private constructor(private val timeout: Long, private val sleepDuration: Int) {
    constructor() : this(500, 100)

    infix fun atMost(timeout: Long): Await = Await(timeout, sleepDuration)

    infix fun every(delay: Int): Await = Await(timeout, delay)

    infix fun until(predicate: () -> Boolean) {
        val currentTime = System.currentTimeMillis()

        while (!predicate()) {
            if (System.currentTimeMillis() - currentTime > timeout) {
                throw TimeOutException
            }

            System.sleep(sleepDuration)
        }
    }
}
