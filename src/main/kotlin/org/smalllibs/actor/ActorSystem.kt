package org.smalllibs.actor

import org.smalllibs.actor.engine.ThreadBasedActorExecution
import org.smalllibs.actor.system.ActorSystemImpl

interface ActorSystem : ActorFor {

    companion object {
        fun system(site: String, execution: ActorExecution = ThreadBasedActorExecution()): ActorSystem {
            return ActorSystemImpl(site, execution)
        }
    }

}
