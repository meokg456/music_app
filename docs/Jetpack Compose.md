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

## *How state work?*

