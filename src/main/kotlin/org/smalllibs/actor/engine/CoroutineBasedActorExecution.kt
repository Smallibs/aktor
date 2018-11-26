package org.smalllibs.actor.engine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class CoroutineBasedActorExecution : AbstractActorExecution() {

    override fun execute(run: () -> Unit) {
        GlobalScope.launch { run() }
    }


}
