# &mu;Actor

Minimal Actor System written in Kotlin. 

# Relationship

Data relationships came from Actor system elaboration and was also inspired by [Akka System](https://doc.akka.io/docs/akka/2.5/general/addressing.html).

![Data Relationships](https://raw.githubusercontent.com/d-plaindoux/actor.kotlin/master/doc/data-relation.png)

# Prototypes

## Behavior&lt;T>

```Kotlin
    typealias Receiver<T> = (Actor<T>, Envelop<T>) -> Unit

    val receiver: Receiver<T>

    fun onStart(actor: Actor<T>)

    fun onResume(actor: Actor<T>)

    fun onPause(actor: Actor<T>)

    fun onStop(actor: Actor<T>)
```

## Actor&lt;T>

### Behavior management

```Kotlin
    fun behavior(): Behavior<T>?

    infix fun start(receiver: Receiver<T>)

    fun start(behavior: Behavior<T>, stacked: Boolean)

    infix fun start(behavior: Behavior<T>)

    fun start(receiver: Receiver<T>, stacked: Boolean)

    fun finish()
```

### Actor Management

```Kotlin
    infix fun <R> actorFor(receiver: Receiver<R>): ActorReference<R>

    fun <R> actorFor(receiver: Receiver<R>, name: String?): ActorReference<R>

    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R>

    fun <R> actorFor(behavior: Behavior<R>, name: String?): ActorReference<R>
```

## ActorContext&lt;T>

```Kotlin
    val self: ActorReference<T>

    fun parent(): ActorReference<*>?

    fun children(): List<ActorReference<*>>
```

## ActorReference&lt;T>

```Kotlin
    val address: ActorAddress

    infix fun tell(envelop: Envelop<T>)

    infix fun tell(content: T)
```

## ActorAddress

```Kotlin
    val name: String

    val parent: ActorAddress?

    infix fun childOf(address: ActorAddress): Boolean

    infix fun parentOf(address: ActorAddress): Boolean
```

# Actor Assertions

## 'tell'


```Kotlin
val called = AtomicReference("")

val system = ActorSystem.system("example")
val reference = system.actorFor<String> { _, m -> called.set(m.content) }

reference tell "Hello World!"

// called value should be "Hello World!
```

## 'become'

```Kotlin
val called = AtomicReference("")

val system = ActorSystem.system("example")
val reference = system.actorFor<String> { a, m ->
    a become { _, v -> called.set("$m.content $v.content") }
}

reference tell "Hello"
reference tell "World!"

// called value should be "Hello World!
```

## 'create'

// ...

# Example

```Kotlin
class PingPong(val sender: ActorReference<PingPong>)

fun player(name: String): Receiver<PingPong> = { actor, message ->
    println("$name playing ...")
    message.content.sender tell PingPong(actor.context.self)
}

fun Game() {
    val system = ActorSystem.system("test")
    
    val ping = system actorFor player("ping")
    val pong = system actorFor player("pong")

    ping tell PingPong(pong)
}
```
