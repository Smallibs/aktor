package org.smalllibs.actor.impl

import org.smalllibs.actor.ActorPath

data class ActorPathImpl(override val name: String, override val parent: ActorPath?) : ActorPath {

    internal constructor(site: String) : this(site, null)

    internal fun newChild(name: String? = null) = ActorPathImpl(name ?: ActorPath.freshName(), this)

}
