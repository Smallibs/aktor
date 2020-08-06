package io.smallibs.utils

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date().getUTCMilliseconds().toLong()
actual fun sleep(duration: Int) = Unit