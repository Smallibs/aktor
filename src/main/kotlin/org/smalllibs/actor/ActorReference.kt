package org.smalllibs.actor

interface ActorReference<T> {

    fun address(): ActorAddress<T>

    infix fun tell(envelop: Envelop<T>)

    infix fun tell(content: T) = tell(Envelop(content))
}
