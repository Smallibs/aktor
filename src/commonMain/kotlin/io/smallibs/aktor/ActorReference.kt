package io.smallibs.aktor

interface ActorReference<T> {

    val address: ActorAddress

    infix fun tell(envelop: Envelop<T>) : ActorReference<T>

    infix fun tell(content: T) : ActorReference<T> {
        tell(Envelop(content))
        return this
    }

}
