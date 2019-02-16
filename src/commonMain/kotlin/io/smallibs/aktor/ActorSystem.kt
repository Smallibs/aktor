package io.smallibs.aktor

import io.smallibs.aktor.bootstrap.Bootstrap

interface ActorSystem : Actor<Any> {

    companion object {
        fun new(site: String, execution: ActorRunner = ActorRunner.coroutine()) = Bootstrap.new(site, execution)
    }

}
