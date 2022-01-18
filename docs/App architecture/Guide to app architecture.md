# Overview
## Common architectural principles

It's important to define an architecture that allows the app to scale, increases the app's robustness and easier to test.

### **Principles:**

- **Separation of concerns**: Separate UI logic, business logic, data transfer.

- **Drive UI from data models**: Data models is independent from UI elements and other app's components. Persistent models are ideal for the following reasons:
    - Users don't lose data if the Android OS destroys your app to free up resources.
    - The app continues to work in cases when a network connection is flaky or not available
    - Make our app more testable and robust.

## Recommended app architecture

![](https://developer.android.com/topic/libraries/architecture/images/mad-arch-overview.png)

## UI layer

![](https://developer.android.com/topic/libraries/architecture/images/mad-arch-ui-overview.png)

The role of UI layer (Presentation layer) is to display the application data on the screen:
- Transform application data into the data that easy to render.
- Listen user input and reflect their effects in the UI data needed

The UI layer is made up of two things:
- UI elements that render data on the screen
- State holder that hold data, expose it to UI, and handle logic

## Domain layer 

![](https://developer.android.com/topic/libraries/architecture/images/mad-arch-overview-domain.png)

The domain layer is responsible for encapsulating complex business logic, or simple business logic that is reused by multiple ViewModels.

- Avoid code duplication.
- Improve readability in domain classes
- Improve testability of the app
- Avoid large classes

Naming convention: Verb + noun/what (optional) + UseCase.

Dependencies: 
Use case classes usually depend on repository classes, and they communicate with the UI layer the same way repositories do—using either callbacks (for Java) or coroutines (for Kotlin)

## Data layer

![](https://developer.android.com/topic/libraries/architecture/images/mad-arch-overview-data.png)

The data layer of an app contains the business logic (Simple app).

The data layer is made of repositories that each can contain zero to many data sources.

Repository classes are responsible for the following tasks:
- Exposing data to the rest of the app.
- Centralizing changes to the data.
- Resolving conflicts between multiple data sources.
- Abstracting sources of data from the rest of the app.
- Containing business logic.

