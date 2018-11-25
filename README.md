# Actor.kotlin

Minimal Actor System written in Kotlin. 

# Example

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
