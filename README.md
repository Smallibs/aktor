# Actor.kotlin

Minimal Actor System written in Kotlin. 

# Example

```Kotlin
val called = AtomicInteger(0)

val system = ActorSystem.system("example")

val reference = system.actorFor<Int> { a, _ ->
    a become { _, v -> called.set(v.content) }
}

reference tell 1
reference tell 42

// called value should be '42'
```
