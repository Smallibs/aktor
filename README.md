Minimal Actor System written in Kotlin. 

# Actor Assertions

## Actor 'tell'


```Kotlin
val called = AtomicReference("")

val system = ActorSystem.system("example")
val reference = system.actorFor<String> { _, m -> called.set(m.content) }

reference tell "Hello World!"

// called value should be "Hello World!
```

## Actor 'become'

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

## Actor 'create'

// TODO

