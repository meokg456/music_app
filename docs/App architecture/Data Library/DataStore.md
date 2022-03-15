# Overview

- Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers.

- DataStore uses Kotlin coroutines and Flow to store data asynchronously, consistently, and transactionally.

- DataStore provides two different implementations:
    - **Preferences DataStore** stores and accesses data using keys. This implementation does not require a predefined schema, and it does not provide type safety.
    - **Proto DataStore** stores data as instances of a custom data type. This implementation requires you to define a schema using protocol buffers, but it provides type safety.

# Setup

## Datastore typed

```kotlin
    // Typed DataStore (Typed API surface, such as Proto)
    dependencies {
        implementation "androidx.datastore:datastore:1.0.0"

        // optional - RxJava2 support
        implementation "androidx.datastore:datastore-rxjava2:1.0.0"

        // optional - RxJava3 support
        implementation "androidx.datastore:datastore-rxjava3:1.0.0"
    }

    // Alternatively - use the following artifact without an Android dependency.
    dependencies {
        implementation "androidx.datastore:datastore-core:1.0.0"
    }
```

## Datastore Preferences

```kotlin
    // Preferences DataStore (SharedPreferences like APIs)
    dependencies {
        implementation("androidx.datastore:datastore-preferences:1.0.0")

        // optional - RxJava2 support
        implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")

        // optional - RxJava3 support
        implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")
    }

    // Alternatively - use the following artifact without an Android dependency.
    dependencies {
        implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    }
```

# Store key-value pairs with Preferences DataStore

## Create a Preferences DataStore

```kotlin
// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
```

## Read from a Preferences DataStore
```kotlin
val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
val exampleCounterFlow: Flow<Int> = context.dataStore.data
  .map { preferences ->
    // No type safety.
    preferences[EXAMPLE_COUNTER] ?: 0
}
```

## Write to a Preferences DataStore

```kotlin
suspend fun incrementCounter() {
  context.dataStore.edit { settings ->
    val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
    settings[EXAMPLE_COUNTER] = currentCounterValue + 1
  }
}
```

# Store typed objects with Proto DataStore

## Define a schema

- Proto DataStore requires a predefined schema in a proto file in the app/src/main/proto/ directory.

```Protobuf
syntax = "proto3";

option java_package = "com.example.application";
option java_multiple_files = true;

message Settings {
  int32 example_counter = 1;
}
```

## Create a Proto DataStore

1. Define a class that implements Serializer<T>, where T is the type defined in the proto file.
2. Use the property delegate created by dataStore to create an instance of DataStore<T>.

```kotlin
object SettingsSerializer : Serializer<Settings> {
  override val defaultValue: Settings = Settings.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): Settings {
    try {
      return Settings.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(
    t: Settings,
    output: OutputStream) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
  fileName = "settings.pb",
  serializer = SettingsSerializer
)
```

## Read from a Proto DataStore
```kotlin
val exampleCounterFlow: Flow<Int> = context.settingsDataStore.data
  .map { settings ->
    // The exampleCounter property is generated from the proto schema.
    settings.exampleCounter
  }
```
## Write to a Proto DataStore
```kotlin
suspend fun incrementCounter() {
  context.settingsDataStore.updateData { currentSettings ->
    currentSettings.toBuilder()
      .setExampleCounter(currentSettings.exampleCounter + 1)
      .build()
    }
}
```