package io.smallibs.aktor

import io.smallibs.aktor.runner.ThreadBasedRunner

fun ActorSystem.Companion.threaded(nbThread: Int? = null): ActorRunner =
    ThreadBasedRunner(nbThread)
