package org.smalllibs.magnet.impl

import org.smalllibs.magnet.ActorPath

data class ActorPathImpl(private val name: String, private val parent: ActorPath?) : ActorPath {

    internal constructor(site: String) : this(site, null)

    internal fun freshChild(name: String? = null) = ActorPathImpl(name ?: ActorPath.freshName(), this)

    override fun name() = this.name

    override fun parent() = parent

}
