# the DeadLetter Aktor



## DeadLetter.Bridge

```kotlin
    infix fun configure(notifier: (ActorReference<*>, Envelop<*>) -> Unit)
```

## Reference the directory

```kotlin
    infix fun DeadLetter.from(reference: ActorReference<*>): DeadLetter.Bridge
```

## A complete example

```kotlin
    val site = Aktor.new("site")
    val deadLetter = DeadLetter from site.system

    deadLetter configure { reference, envelop -> 
        // Notification of rejected envelop for a given actor 
    }
```

