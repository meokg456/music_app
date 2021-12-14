# Jetpack Compose

## *What is Jetpack Compose?*

- Jetpack Compose is a modern declarative UI Toolkit for Android

- Jetpack Compose provide declarative APIs to describe your App UI without imperative XML files

## *Why we should use Jetpack Compose?*

The declarative programming paradigm will avoid the likelihood of errors: 

- A piece of data can be rendered in many places. And it's easy to forget to update some of the views that show it.

- It's easy to create illegal states. For instances, we might set a value of a node that was removed from the UI.

- The software grows up with the number of views that require updating

Dynamic content

```kotlin
@Composable
fun Greeting(names: List<String>) {
    for (name in names) {
        Text("Hello $name")
    }
}
```

- This function can create a list of UI depend on list of data.
- Otherwise, we can use `if` to create UI with conditions


## *How Jetpack Compose work?*
```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello $name")
}
```
This is a simple `@Composable` function that use `name` to render a text widget on the screen
- All of composable functions must have the `@Composable` annotation.

- Composable functions accept parameters which allow the app logic to describe the UI

- The function doesn't return anything because they describe the desired screen state instead of constructing UI widgets

- This function is fast, idempotent, and free of side-effects.

- In fact, widgets are not exposed as objects. You update the UI by calling composable funtion with different arguments.

Composable funtion will use the data to describe the UI

![](https://developer.android.com/images/jetpack/compose/mmodel-flow-data.png)

And when the user interacts with the UI, UI raises events such as `onClick`. Those events will notify app logic to change the app's state. When app's state changes, the composable functions are called with the new data

![](https://developer.android.com/images/jetpack/compose/mmodel-flow-events.png)

- Composable functions can execute in any order

- Composable functions can run in parallel

- Recomposition skips as much as possible

- Recomposition is optimistic. Recomposition start whenever the parameters changes. If the parameters changes while recomposition the recomposition will be cancelled and restart with the new parameters.

- Composable functions might run quite frequently

# State management

## *What is state?*

- Composable function describe UI with arguments. These arguments are presentations of the UI state.

## *Why we need to managing state?*

- In some cases, the next state need the current state data to render. So we need to store that data for the next use.

## *How to manage state?*

### **State in composables**

- Composable functions can store a single object in memory by using the `remember` composable. 

- mutableStateOf creates an observable MutableState<T>

- There are three ways to declare a MutableState object in a composable:
```kotlin
    - val mutableState = remember { mutableStateOf(default) }
    - var value by remember { mutableStateOf(default) }
    - val (value, setValue) = remember { mutableStateOf(default) }
```
### **Other supported type of state**

Jetpack Compose doesn't require that you use MutableState<T> to hold state. We also can use other supported type of state:
- LiveData
- Flow
- RxJava2

### **State hoisting**

- In short, move the event that will change the composable state to the parameter to make stateful become stateless.

### **Restore state in Compose**

- Use `rememberSaveable` to restore your UI state after an activity or process is recreated.

**Parcelize**

```kotlin
@Parcelize
data class City(val name: String, val country: String) : Parcelable

@Composable
fun CityScreen() {
    var selectedCity = rememberSaveable {
        mutableStateOf(City("Madrid", "Spain"))
    }
}
```

**MapSaver**

```kotlin
data class City(val name: String, val country: String)

val CitySaver = run {
    val nameKey = "Name"
    val countryKey = "Country"
    mapSaver(
        save = { mapOf(nameKey to it.name, countryKey to it.country) },
        restore = { City(it[nameKey] as String, it[countryKey] as String) }
    )
}

@Composable
fun CityScreen() {
    var selectedCity = rememberSaveable(stateSaver = CitySaver) {
        mutableStateOf(City("Madrid", "Spain"))
    }
}
```

**ListSaver**
```kotlin
data class City(val name: String, val country: String)

val CitySaver = run {
    val nameKey = "Name"
    val countryKey = "Country"
    mapSaver(
        save = { mapOf(nameKey to it.name, countryKey to it.country) },
        restore = { City(it[nameKey] as String, it[countryKey] as String) }
    )
}

@Composable
fun CityScreen() {
    var selectedCity = rememberSaveable(stateSaver = CitySaver) {
        mutableStateOf(City("Madrid", "Spain"))
    }
}
```

### **Managing state in Compose**

- Composables for simple UI element state management.

- State holders for complex UI element state management. They own UI elements' state and UI logic.

![](https://developer.android.com/images/jetpack/compose/state-dependencies.svg)

**Types of state and logic**

States:

- **UI element state** is the hoisted state of UI elements.

- **Screen or UI state** is what needs to be displayed on the screen.

logic:

- **UI behavior logic or UI logic** is related to how to display state changes on the screen.

- **Business logic** is what to do with state changes.

### **Composables as source of truth**

- Having UI logic and UI elements state in composables is a good approach if the state and logic is simple.

```kotlin
@Composable
fun MyApp() {
    MyTheme {
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()

        Scaffold(scaffoldState = scaffoldState) {
            MyContent(
                showSnackbar = { message ->
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }
    }
}
```

### **State holders as source of truth**

