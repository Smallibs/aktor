package io.smallibs.aktor

import io.smallibs.aktor.system.ActorSystemImpl

interface ActorSystem : Actor<Any> {

    companion object {
        fun system(site: String, execution: ActorRunner = ActorRunner.threaded()): ActorSystem =
            ActorSystemImpl(site, execution)
    }
}
