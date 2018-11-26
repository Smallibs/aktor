package org.smalllibs.actor.engine

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.smalllibs.actor.ActorExecution
import org.smalllibs.actor.impl.ActorImpl
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

interface ActorRunner {

    fun execute(run: () -> Unit)


}
