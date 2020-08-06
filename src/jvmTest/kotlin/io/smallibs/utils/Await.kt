package io.smallibs.utils

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun sleep(duration: Int) = Thread.sleep(duration.toLong())