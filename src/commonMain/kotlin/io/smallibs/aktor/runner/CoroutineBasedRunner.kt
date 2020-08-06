package io.smallibs.aktor.runner

import io.smallibs.aktor.ActorRunner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CoroutineBasedRunner : ActorRunner {

    override fun execute(run: () -> Unit) {
        GlobalScope.launch { run() }
    }
}
