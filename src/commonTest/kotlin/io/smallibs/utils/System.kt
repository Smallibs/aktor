package io.smallibs.utils

expect object System {
    fun currentTimeMillis(): Long
    fun sleep(duration: Int)
}
