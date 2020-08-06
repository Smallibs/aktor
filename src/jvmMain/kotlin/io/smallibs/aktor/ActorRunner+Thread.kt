package io.smallibs.aktor

import io.smallibs.aktor.runner.ThreadBasedRunner

fun ActorRunner.Companion.threaded(nbThread: Int? = null): ActorRunner = ThreadBasedRunner(nbThread)
