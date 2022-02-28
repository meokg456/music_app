# Handling Lifecycle with Lifecycle-Aware Components

Lifecycle-Aware Components is a components which can observe lifecycle status of another component, such as Activities, fragments.

Lifecycle-Aware Components will produce a better-organized, and often lighter-weight code, that is easier to maintain.

Before:

```kotlin
internal class MyLocationListener(
        private val context: Context,
        private val callback: (Location) -> Unit
) {

    fun start() {
        // connect to system location service
    }

    fun stop() {
        // disconnect from system location service
    }
}

class MyActivity : AppCompatActivity() {
    private lateinit var myLocationListener: MyLocationListener

    override fun onCreate(...) {
        myLocationListener = MyLocationListener(this) { location ->
            // update UI
        }
    }

    public override fun onStart() {
        super.onStart()
        myLocationListener.start()
        // manage other components that need to respond
        // to the activity lifecycle
    }

    public override fun onStop() {
        super.onStop()
        myLocationListener.stop()
        // manage other components that need to respond
        // to the activity lifecycle
    }
}
```

Weakness:
- Many method call, logic when the activities, fragments is complex. This make the app hard to maintain.
- In case the component is starting but the activites, fragments has stopped. The component will call stop on nothing and then the component finish started up. The component will alive longer than it's needed.

After:
- Simple logic
```kotlin
class MyObserver : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        connect()
    }

    override fun onPause(owner: LifecycleOwner) {
        disconnect()
    }
}

myLifecycleOwner.getLifecycle().addObserver(MyObserver())
```

- Complex logic

```kotlin
internal class MyLocationListener(
        private val context: Context,
        private val lifecycle: Lifecycle,
        private val callback: (Location) -> Unit
): DefaultLifecycleObserver {

    private var enabled = false

    override fun onStart(owner: LifecycleOwner) {
        if (enabled) {
            // connect
        }
    }

    fun enable() {
        enabled = true
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            // connect if not connected
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        // disconnect if connected
    }
}
```

```kotlin
class MyActivity : AppCompatActivity() {
    private lateinit var myLocationListener: MyLocationListener

    override fun onCreate(...) {
        myLocationListener = MyLocationListener(this, lifecycle) { location ->
            // update UI
        }
        Util.checkUserStatus { result ->
            if (result) {
                myLocationListener.enable()
            }
        }
    }
}
```

# Lifecycle

`Lifecycle` is a class that holds the information about the lifecycle state of a component (like an activity or a fragment) and allows other objects to observe this state.

`Lifecycle` uses two main enumerations to track the lifecycle status for its associated component:

## Event
The lifecycle events that are dispatched from the framework and the `Lifecycle` class. These events map to the callback events in activities and fragments.
## State

The current state of the component tracked by the `Lifecycle` object.

![](https://developer.android.com/images/topic/libraries/architecture/lifecycle-states.svg)

```kotlin
class MyObserver : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        connect()
    }

    override fun onPause(owner: LifecycleOwner) {
        disconnect()
    }
}

myLifecycleOwner.getLifecycle().addObserver(MyObserver())
```

# LifecycleOwner

`LifecycleOwner` is a single method interface that denotes that the class has a `Lifecycle`.

It has one method, `getLifecycle()`, which must be implemented by the class

```kotlin
class MyActivity : Activity(), LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    public override fun onStart() {
        super.onStart()
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}
```