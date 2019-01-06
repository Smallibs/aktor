package io.smallibs.utils

import kotlin.js.Date

actual fun getCurrentTime(): Long =
    Date().getUTCMilliseconds().toLong()

actual fun waitFor(duration: Int) = Unit