# Aktor

[![Build Status](https://travis-ci.org/Smallibs/aktor.svg?branch=master)](https://travis-ci.org/Smallibs/aktor)

Multiplatform Actor System written in Kotlin. 

# Relationship

Data relationships has been inspired by [Akka System](https://doc.akka.io/docs/akka/2.5/general/addressing.html).

![Data Relationships](https://raw.githubusercontent.com/d-plaindoux/actor.kotlin/master/doc/images/data-relation.png)

# Prototypes

## Behavior&lt;T>

```Kotlin
    typealias CoreReceiver<T> = (Actor<T>, CoreEnvelop<T>) -> Unit
    typealias ProtocolReceiver<T> = (Actor<T>, ProtocolEnvelop<T>) -> Unit

    val core: CoreReceiver<T>
    
    val protocol: ProtocolReceiver<T>

    fun onStart(actor: Actor<T>)

    fun onResume(actor: Actor<T>)

    fun onPause(actor: Actor<T>)

    fun onFinish(actor: Actor<T>)
```

## Actor&lt;T>

### Behavior management

```Kotlin
    val context: ActorContext<T>

    fun behavior(): Behavior<T>

    infix fun become(receiver: Receiver<T>)

    fun become(receiver: ProtocolReceiver<T>, stacked: Boolean)

    infix fun become(behavior: Behavior<T>)

    fun become(behavior: Behavior<T>, stacked: Boolean)

    fun unbecome()
    
    fun kill() : Boolean
```

### Actor Builder

```Kotlin
    infix fun <R> actorFor(property: ActorProperty<R>): ActorReference<R>

    infix fun <R> actorFor(protocolReceiver: ProtocolReceiver<R>): ActorReference<R>

    fun <R> actorFor(protocol: ProtocolReceiver<R>, name: String): ActorReference<R>

    infix fun <R> actorFor(behavior: Behavior<R>): ActorReference<R>

    fun <R> actorFor(behavior: Behavior<R>, name: String): ActorReference<R>
```

## ActorContext&lt;T>

```Kotlin
    val self: ActorReference<T>

    fun parent(): ActorReference<*>?

    fun children(): Collection<ActorReference<*>>
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
val called = atomic("")

val system = Aktor.new("example")
val reference = system.actorFor<String> { _, m -> 
    called.set(m.content) 
}

reference tell "Hello World!"

// called value should be "Hello World!
```

## 'become'

```Kotlin
val called = atomic("")

val system = Aktor.new("example")
val reference = system.actorFor<String> { a, m ->
    a become { _, v -> 
        called.set("$m.content $v.content") 
    }
}

reference tell "Hello"
reference tell "World!"

// called value should be "Hello World!
```

## 'create'

```Kotlin
val called = atomic("")

data class Create(name: String)

val system = Aktor.new("example")
val reference = system.actorFor<Create> { a, e -> 
    a.actorFor<String>({ _, v -> 
       called.set("$m.content $v.content") 
    }, e.name)
}

reference tell Create("Hello")
// ...
```

# Basic Ping Pong Example

```Kotlin
data class PingPong(val sender: ActorReference<PingPong>)

fun player(name: String): Receiver<PingPong> = { actor, message ->
    println("$name playing ...")
    message.content.sender tell PingPong(actor.context.self)
}

fun Game() {
    val system = Aktor.new("test")
    
    val ping = system actorFor player("ping")
    val pong = system actorFor player("pong")

    ping tell PingPong(pong)
}
```

# License

Copyright 2019 D. Plaindoux.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
