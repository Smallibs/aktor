package io.smallibs.utils 

actual fun getCurrentTime() : Long =
    System.currentTimeMillis()

actual fun waitFor(duration: Int) =
    Thread.sleep(duration.toLong())