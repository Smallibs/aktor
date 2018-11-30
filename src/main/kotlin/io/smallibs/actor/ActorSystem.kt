package io.smallibs.actor

import io.smallibs.actor.engine.ActorRunner
import io.smallibs.actor.engine.ThreadBasedRunner
import io.smallibs.actor.system.ActorSystemImpl

interface ActorSystem : ActorBuilder {

    companion object {
        fun system(site: String, execution: ActorRunner = ThreadBasedRunner()): ActorSystem {
            return ActorSystemImpl(site, execution)
        }
    }

}
