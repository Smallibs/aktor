package io.smallibs.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

actual fun currentTimeMillis(): Long = System.currentTimeMillis()
actual fun sleep(duration: Int) = runBlocking { delay(duration.toLong()) }