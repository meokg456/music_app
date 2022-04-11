# Overview
- `WorkManager` is the recommended solution for persistent work. Work is persistent when it remains scheduled through app restarts and system reboots.

- Because most background processing is best accomplished through persistent work, WorkManager is the primary recommended API for background processing.

## Types of persistent work

- Three types:
    - **Immediate**: Tasks that must begin immediately and complete soon. May be expedited.
    - **Long Running**: Tasks which might run for longer, potentially longer than 10 minutes.
    - **Deferrable**: Scheduled tasks that start at a later time and can run periodically.

![](https://developer.android.com/images/guide/background/workmanager_main.svg)

# Features

## Work constraints

- Constraints ensure that work is deferred until optimal conditions are met.

## Robust scheduling

- WorkManager allows you to schedule work to run one-time or repeatedly using flexible scheduling windows.

- Work can be tagged and named as well, allowing you to schedule unique, replaceable work and monitor or cancel groups of work together.

- Scheduled work is stored in an internally managed SQLite database and WorkManager takes care of ensuring that this work persists and is rescheduled across device reboots.

- In addition, WorkManager adheres to power-saving features and best practices like Doze mode, so you don't have to worry about it.

## Expedited work

- You can use WorkManager to schedule immediate work for execution in the background. You should use Expedited work for tasks that are important to the user and which complete within a few minutes.

## Flexible retry policy

- Sometimes work fails. WorkManager offers flexible retry policies, including a configurable exponential backoff policy.

## Work chaining

- For complex related work, chain individual work tasks together using an intuitive interface that allows you to control which pieces run sequentially and which run in parallel.

## Built-In threading interoperability

- WorkManager integrates seamlessly with Coroutines and RxJava and provides the flexibility to plug in your own asynchronous APIs.

## Use WorkManager for reliable work

- WorkManager is intended for work that is required to run reliably even if the user navigates off a screen, the app exits, or the device restarts. 

# Getting started

## Setup 
```kotlin
dependencies {
    val work_version = "2.7.1"

    // (Java only)
    implementation("androidx.work:work-runtime:$work_version")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$work_version")

    // optional - RxJava2 support
    implementation("androidx.work:work-rxjava2:$work_version")

    // optional - GCMNetworkManager support
    implementation("androidx.work:work-gcm:$work_version")

    // optional - Test helpers
    androidTestImplementation("androidx.work:work-testing:$work_version")

    // optional - Multiprocess support
    implementation "androidx.work:work-multiprocess:$work_version"
}
```

## Define a work

- Work is defined using the `Worker` class. 
- The `doWork()` method runs asynchronously on a background thread provided by WorkManager.

```kotlin
class UploadWorker(appContext: Context, workerParams: WorkerParameters):
       Worker(appContext, workerParams) {
   override fun doWork(): Result {

       // Do the work here--in this case, upload the images.
       uploadImages()

       // Indicate whether the work finished successfully with the Result
       return Result.success()
   }
}
```

- The `Result` returned from `doWork()`:
    - Result.success(): The work finished successfully.
    - Result.failure(): The work failed.
    - Result.retry(): The work failed and should be tried at another time according to its retry policy.

## Create a WorkRequest

**One time:**

```kotlin
val uploadWorkRequest: WorkRequest =
   OneTimeWorkRequestBuilder<UploadWorker>()
       .build()
```

**Expedited work:**

- Expedited work allows WorkManager to execute important work while giving the system better control over access to resources.

Expedited work is notable for the following characteristics:

- **Importance**: Expedited work suits tasks which are important to the user or are user-initiated.
- **Speed**: Expedited work best fits short tasks that start immediately and complete within a few minutes.
- **Quotas**: A system-level quota that limits foreground execution time determines whether an expedited job can start.
- **Power Management**t: Power management restrictions, such as Battery Saver and Doze, are less likely to affect expedited work.
- **Latency**: The system immediately executes expedited work, provided that the system's current workload enables it to do so. This means they are latency sensitive and can't be scheduled for later execution.

Examples: Send images in chat, handle payments or subscription

```kotlin
val request = OneTimeWorkRequestBuilder()
    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
    .build()
```

**Periodic work**

```Kotlin
val saveRequest =
       PeriodicWorkRequestBuilder<SaveImageToFileWorker>(1, TimeUnit.HOURS)
    // Additional configuration
           .build()
```

- Flexible run intervals:

```kotlin
val myUploadWork = PeriodicWorkRequestBuilder<SaveImageToFileWorker>(
       1, TimeUnit.HOURS, // repeatInterval (the period cycle)
       15, TimeUnit.MINUTES) // flexInterval
    .build()
```

## Enqueue work request

```kotlin
WorkManager.getInstance(context)
    .enqueue(request)
```

## Work Constraints

| Constraint | Description |
| ---------- | ----------- |
|NetworkType|Constrains the type of network required for your work to run. For example, Wi-Fi (UNMETERED).|
|BatteryNotLow|When set to true, your work will not run if the device is in low battery mode.|
|RequiresCharging|When set to true, your work will only run when the device is charging.|
|DeviceIdle|When set to true, this requires the user’s device to be idle before the work will run. This can be useful for running batched operations that might otherwise have a negative performance impact on other apps running actively on the user’s device.|
|StorageNotLow|When set to true, your work will not run if the user’s storage space on the device is too low.|

```kotlin
val constraints = Constraints.Builder()
   .setRequiredNetworkType(NetworkType.UNMETERED)
   .setRequiresCharging(true)
   .build()

val myWorkRequest: WorkRequest =
   OneTimeWorkRequestBuilder<MyWork>()
       .setConstraints(constraints)
       .build()
```

## Delayed work
```kotlin
val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
   .setInitialDelay(10, TimeUnit.MINUTES)
   .build()
```

## Retry and backoff policy

```kotlin
val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
   .setBackoffCriteria(
       BackoffPolicy.LINEAR,
       OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
       TimeUnit.MILLISECONDS)
   .build()
```

## Tag work
- Tagging allows you to operate with a group of work requests together.

For examples: `WorkManager.cancelAllWorkByTag(String)`, `WorkManager.getWorkInfosByTag(String)`

```kotlin
val myWorkRequest = OneTimeWorkRequestBuilder<MyWork>()
   .addTag("cleanup")
   .build()
```

## Assign input data 

```kotlin
// Define the Worker requiring input
class UploadWork(appContext: Context, workerParams: WorkerParameters)
   : Worker(appContext, workerParams) {

   override fun doWork(): Result {
       val imageUriInput =
           inputData.getString("IMAGE_URI") ?: return Result.failure()

       uploadFile(imageUriInput)
       return Result.success()
   }
   ...
}

// Create a WorkRequest for your Worker and sending it input
val myUploadWork = OneTimeWorkRequestBuilder<UploadWork>()
   .setInputData(workDataOf(
       "IMAGE_URI" to "http://..."
   ))
   .build()
```

# Work states

## One-time work states

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/one-time-work-flow.png)

## Periodic work states

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/periodic-work-states.png)

# Managing work

## Unique work

- `WorkManager.enqueueUniqueWork()` for one time work
- `WorkManager.enqueueUniquePeriodicWork()` for periodic work

Both of these methods accept 3 arguments:

- `uniqueWorkName` - A String used to uniquely identify the work request.
- `existingWorkPolicy` - An enum which tells WorkManager what to do if there's already an unfinished chain of work with that unique name.
- `work` - the WorkRequest to schedule.

```kotlin
val sendLogsWorkRequest =
       PeriodicWorkRequestBuilder<SendLogsWorker>(24, TimeUnit.HOURS)
           .setConstraints(Constraints.Builder()
               .setRequiresCharging(true)
               .build()
            )
           .build()
WorkManager.getInstance(this).enqueueUniquePeriodicWork(
           "sendLogs",
           ExistingPeriodicWorkPolicy.KEEP,
           sendLogsWorkRequest
)
```

- There are 4 options in `ExistingWorkPolicy`
    - `REPLACE` existing work with the new work. This option cancels the existing work.
    - `KEEP` existing work and ignore the new work.
    - `APPEND` the new work to the end of the existing work. This policy will cause your new work to be chained to the existing work, running after the existing work finishes.
    - `APPEND_OR_REPLACE` functions similarly to APPEND, except that it is not dependent on prerequisite work status. If the existing work is CANCELLED or FAILED, the new work still runs.

## Observing your work

```kotlin
// by id
workManager.getWorkInfoById(syncWorker.id) // ListenableFuture<WorkInfo>

// by name
workManager.getWorkInfosForUniqueWork("sync") // ListenableFuture<List<WorkInfo>>

// by tag
workManager.getWorkInfosByTag("syncTag") // ListenableFuture<List<WorkInfo>>
```
We can observe changes to the `WorkInfo` by registering a listener.
```kotlin
workManager.getWorkInfoByIdLiveData(syncWorker.id)
               .observe(viewLifecycleOwner) { workInfo ->
   if(workInfo?.state == WorkInfo.State.SUCCEEDED) {
       Snackbar.make(requireView(), 
      R.string.work_completed, Snackbar.LENGTH_SHORT)
           .show()
   }
}
```

**Complex work queries**

```kotlin
val workQuery = WorkQuery.Builder
       .fromTags(listOf("syncTag"))
       .addStates(listOf(WorkInfo.State.FAILED, WorkInfo.State.CANCELLED))
       .addUniqueWorkNames(listOf("preProcess", "sync")
    )
   .build()

val workInfos: ListenableFuture<List<WorkInfo>> = workManager.getWorkInfos(workQuery)
```

## Cancelling and stopping work

```kotlin 
// by id
workManager.cancelWorkById(syncWorker.id)

// by name
workManager.cancelUniqueWork("sync")

// by tag
workManager.cancelAllWorkByTag("syncTag")
```

## Stop a running Worker
There are a few different reasons your running Worker might be stopped by WorkManager:
- You explicitly asked for it to be cancelled
- In the case of unique work, you explicitly enqueued a new WorkRequest with an ExistingWorkPolicy of REPLACE. The old WorkRequest is immediately considered cancelled.
- Your work's constraints are no longer met.
- The system instructed your app to stop your work for some reason. This can happen if you exceed the execution deadline of 10 minutes.

**onStopped() callback**

WorkManager invokes `ListenableWorker.onStopped()` as soon as your Worker has been stopped.

**isStopped() property**

You can call the `ListenableWorker.isStopped()` method to check if your worker has already been stopped.

# Observing intermediate Worker progress

## Updating Progress

```kotlin
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class ProgressWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        delay(delayDuration)
        setProgress(lastUpdate)
        return Result.success()
    }
}
```

## Observing Progress

```kotlin
WorkManager.getInstance(applicationContext)
    // requestId is the WorkRequest id
    .getWorkInfoByIdLiveData(requestId)
    .observe(observer, Observer { workInfo: WorkInfo? ->
            if (workInfo != null) {
                val progress = workInfo.progress
                val value = progress.getInt(Progress, 0)
                // Do something with progress information
            }
    })
```

# Chaining work
```kotlin
WorkManager.getInstance(myContext)
   // Candidates to run in parallel
   .beginWith(listOf(plantName1, plantName2, plantName3))
   // Dependent work (only runs after all previous work in chain)
   .then(cache)
   .then(upload)
   // Call enqueue to kick things off
   .enqueue()
```

## Input Mergers

**OverwritingInputMerger**

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-overwriting-merger-conflict.png)

**ArrayCreatingInputMerger**

```kotlin
val cache: OneTimeWorkRequest = OneTimeWorkRequestBuilder<PlantWorker>()
   .setInputMerger(ArrayCreatingInputMerger::class)
   .setConstraints(constraints)
   .build()
```

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-array-merger-example.png)

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-array-merger-conflict.png)

## Chaining and Work Statuses
Enqueued: 

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-enqueued-all-blocked.png)

First succeed:

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-enqueued-in-progress.png)

Retry:

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-enqueued-retry.png)

Failed:
![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-enqueued-failed.png)

Cancelled:

![](https://developer.android.com/images/topic/libraries/architecture/workmanager/how-to/chaining-enqueued-cancelled.png)

# Testing Worker implementation

## Testing Workers

```kotlin
class SleepWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    override fun doWork(): Result {
        // Sleep on a background thread.
        val sleepDuration = inputData.getLong(SLEEP_DURATION, 1000)
        Thread.sleep(sleepDuration)
        return Result.success()
    }

    companion object {
        const val SLEEP_DURATION = "SLEEP_DURATION"
    }
}
```

```kotlin
// Kotlin code uses the TestWorkerBuilder extension to build
// the Worker
@RunWith(AndroidJUnit4::class)
class SleepWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun testSleepWorker() {
        val worker = TestWorkerBuilder<SleepWorker>(
            context = context,
            executor = executor,
            inputData = workDataOf("SLEEP_DURATION" to 1000L)
        ).build()

        val result = worker.doWork()
        assertThat(result, `is`(Result.success()))
    }
}
```

## Testing ListenableWorker and its variants
```kotlin
class SleepWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        delay(1000L) // milliseconds
        return Result.success()
    }
}
```


```kotlin
@RunWith(AndroidJUnit4::class)
class SleepWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testSleepWorker() {
        val worker = TestListenableWorkerBuilder<SleepWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }
    }
}
```

# Integration tests with WorkManager 

## Setup

```kotlin
dependencies {
    val work_version = "2.4.0"

    ...

    // optional - Test helpers
    androidTestImplementation("androidx.work:work-testing:$work_version")
}
```

## Basic test

```kotlin
class EchoWorker(context: Context, parameters: WorkerParameters)
   : Worker(context, parameters) {
   override fun doWork(): Result {
       return when(inputData.size()) {
           0 -> Result.failure()
           else -> Result.success(inputData)
       }
   }
}
```

```kotlin
@Test
@Throws(Exception::class)
fun testSimpleEchoWorker() {
    // Define input data
    val input = workDataOf(KEY_1 to 1, KEY_2 to 2)

    // Create request
    val request = OneTimeWorkRequestBuilder<EchoWorker>()
        .setInputData(input)
        .build()

    val workManager = WorkManager.getInstance(applicationContext)
    // Enqueue and wait for result. This also runs the Worker synchronously
    // because we are using a SynchronousExecutor.
    workManager.enqueue(request).result.get()
    // Get WorkInfo and outputData
    val workInfo = workManager.getWorkInfoById(request.id).get()
    val outputData = workInfo.outputData

    // Assert
    assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
    assertThat(outputData, `is`(input))
}
```

```kotlin
@Test
@Throws(Exception::class)
fun testEchoWorkerNoInput() {
   // Create request
   val request = OneTimeWorkRequestBuilder<EchoWorker>()
       .build()

   val workManager = WorkManager.getInstance(applicationContext)
   // Enqueue and wait for result. This also runs the Worker synchronously
   // because we are using a SynchronousExecutor.
   workManager.enqueue(request).result.get()
   // Get WorkInfo
   val workInfo = workManager.getWorkInfoById(request.id).get()

   // Assert
   assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
}
```

## Simulating constraints, delays, and periodic work

- Delay: testDriver.setInitialDelayMet(request.id)

- Constraints: testDriver.setAllConstraintsMet(request.id)

- Period delay: testDriver.setPeriodDelayMet(request.id)
