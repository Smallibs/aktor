package org.smalllibs.actor.impl

import org.smalllibs.actor.ActorPath

data class ActorPathImpl(private val name: String, private val parent: ActorPath?) : ActorPath {

    internal constructor(site: String) : this(site, null)

    internal fun newChild(name: String? = null) = ActorPathImpl(name ?: ActorPath.freshName(), this)

    override fun name() = this.name

    override fun parent() = parent

}
