package io.smallibs.aktor

import io.smallibs.aktor.engine.ActorRunner
import io.smallibs.aktor.engine.ThreadBasedRunner
import io.smallibs.aktor.system.ActorSystemImpl

interface ActorSystem : Actor<Any> {

    companion object {
        fun system(site: String, execution: ActorRunner = ActorRunner.threaded()): ActorSystem {
            return ActorSystemImpl(site, execution)
        }
    }

}
