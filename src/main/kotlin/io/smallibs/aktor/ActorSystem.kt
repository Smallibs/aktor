package io.smallibs.aktor

import io.smallibs.aktor.engine.ActorRunner
import io.smallibs.aktor.engine.ThreadBasedRunner
import io.smallibs.aktor.system.ActorSystemImpl

interface ActorSystem : ActorBuilder {

    companion object {
        fun system(site: String, execution: ActorRunner = ThreadBasedRunner()): ActorSystem {
            return ActorSystemImpl(site, execution)
        }
    }

}
