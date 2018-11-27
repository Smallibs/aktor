package org.smalllibs.actor

import org.smalllibs.actor.engine.ActorRunner
import org.smalllibs.actor.engine.ThreadBasedRunner
import org.smalllibs.actor.system.ActorSystemImpl

interface ActorSystem : ActorBuilder {

    companion object {
        fun system(site: String, execution: ActorRunner = ThreadBasedRunner()): ActorSystem {
            return ActorSystemImpl(site, execution)
        }
    }

}
