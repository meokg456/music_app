# Overview

- The Paging library helps you load and display pages of data from a larger dataset from local storage or over network.

Benefits:

- In-memory caching for your paged data. This ensures that your app uses system resources efficiently while working with paged data.

- Built-in request deduplication, ensuring that your app uses network bandwidth and system resources efficiently.

- Configurable `RecyclerView` adapters that automatically request data as the user scrolls toward the end of the loaded data.

- First-class support for Kotlin coroutines and Flow, as well as LiveData and RxJava.

- Built-in support for error handling, including refresh and retry capabilities.

# Setup
```gradle
dependencies {
  val paging_version = "3.1.0"

  implementation("androidx.paging:paging-runtime:$paging_version")

  // alternatively - without Android dependencies for tests
  testImplementation("androidx.paging:paging-common:$paging_version")

  // optional - RxJava2 support
  implementation("androidx.paging:paging-rxjava2:$paging_version")

  // optional - RxJava3 support
  implementation("androidx.paging:paging-rxjava3:$paging_version")

  // optional - Guava ListenableFuture support
  implementation("androidx.paging:paging-guava:$paging_version")

  // optional - Jetpack Compose integration
  implementation("androidx.paging:paging-compose:1.0.0-alpha14")
}
```

# Library architecture
**3 Layers:**
- The repository layer
- The ViewModel layer
- The UI layer

## Repository layer

- The primary Paging library component in the repository layer is `PagingSource`.

- Each `PagingSource` object defines a source of data and how to retrieve data from that source.

- `RemoteMediator` object handles paging from a layered data source, such as a network data source with a local database cache.

## ViewModel layer

- The `Pager` component provides a public API for constructing instances of `PagingData` that are exposed in reactive streams, based on a `PagingSource` object and a `PagingConfig` configuration object.

- The component that connects the `ViewModel` layer to the UI is `PagingData`. A `PagingData` object is a container for a snapshot of paginated data. It queries a `PagingSource` object and stores the result.

## UI layer

- The primary Paging library component in the UI layer is `PagingDataAdapter`, a `RecyclerView` adapter that handles paginated data.

- Alternatively, you can use the included `AsyncPagingDataDiffer` component to build your own custom adapter.