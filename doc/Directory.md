# the Directory Aktor

## Directory.Bridge

```kotlin
    fun <T : Any> Directory.onSearchComplete(
        success: (ActorReference<T>) -> Unit,
        failure: () -> Unit = { }
    ): ProtocolReceiver<Directory.SearchActorResponse<T>>
```

```kotlin
    inline infix fun <reified T : Any> register(reference: ActorReference<T>)

    inline infix fun <reified T : Any> find(receptor: ActorReference<Directory.SearchActorResponse<T>>)

    inline fun <reified T : Any> find(name: String, receptor: ActorReference<Directory.SearchActorResponse<T>>)

    infix fun unregister(reference: ActorReference<*>)
```

## Reference the directory

```kotlin
    infix fun Directory.from(reference: ActorReference<*>): Directory.Bridge
```

## A complete example

```kotlin
    val site = Aktor.new("site")
    val directory = Directory from site.system

    directory register (site.actorFor(TestActor.receiver, "test"))

    directory.find("test", site actorFor onSearchComplete<TestActor.Protocol>({ reference -> 
        // do something with the reference    
    }, { 
        // Not found ...
    }))
```