package io.smallibs.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.promise
import kotlin.js.Date

actual object System {
    actual fun currentTimeMillis(): Long =
        Date().getUTCMilliseconds().toLong()

    actual fun sleep(duration: Int): dynamic =
        GlobalScope.promise { delay(duration.toLong()) }
}
