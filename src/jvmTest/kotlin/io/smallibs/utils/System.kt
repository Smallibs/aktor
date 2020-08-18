package io.smallibs.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

actual object System {
    actual fun currentTimeMillis(): Long =
        java.lang.System.currentTimeMillis()

    actual fun sleep(duration: Int) =
        runBlocking { delay(duration.toLong()) }
}
