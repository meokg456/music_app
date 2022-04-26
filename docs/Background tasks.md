# Overview

- Processing data in the background is an important part of creating an Android application that is both responsive for your users as well as a good citizen on the Android platform.

- Doing work on the main thread can lead to poor performance and therefore a poor user experience.

## Guiding principle
In general, you should take any blocking tasks off the UI thread. Common long-running tasks include things like decoding a bitmap, accessing storage, working on a machine learning (ML) model, or performing network requests.

## Definition of background work

- None of the app's activities are currently visible to the user.
- The app isn't running any foreground services that started while an activity from the app was visible to the user.

## Common types of background work

![](https://developer.android.com/images/guide/background/background.svg)

|Category|Persistent|Impersistent|
|--------|----------|------------|
|Immediate|WorkManager|Coroutines|
|Long running|WorkManager|Not recommended. Instead, perform work persistently using WorkManager.|
|Deferrable|WorkManager|Not recommended. Instead, perform work persistently using WorkManager.|

## Immediate work

- Immediate work encompasses tasks which need to execute right away.

- For persistent immediate work, use WorkManager with a expedited OneTimeWorkRequest.

- For impersistent immediate work, you should you use Kotlin coroutines.

## Long-running work

- Work is long running if it is likely to take more than ten minutes to complete.

- WorkManager allows you to handle such tasks using a long-running Worker.

## Deferrable work

- Deferrable work is any work that does not need to run right away.

- Scheduling deferred work through WorkManager is the best way to handle tasks that don't need to run immediately but which ought to remain scheduled when the app closes or the device restarts.

## Alarms

- You should only use AlarmManager only for scheduling exact alarms such as alarm clocks or calendar events.

- When using AlarmManager to schedule background work, it wakes the device from Doze mode and its use can therefore have a negative impact on battery life and overall system health.

# Comparing between background tasks technique

## Impersistent tasks 

|Background Threads|Coroutine|
|-|-|
|Traditional method|Lightweight method|
|Create new thread to execute tasks for the app|Easily run tasks in background thread|
|Complicate to communicate with main thread and be careful with variable instance|Easier to communicate with main thead and be limit by scope|

## Persistent tasks

|Broadcast receiver|Alarm manager|Work manager|
|-|-|-|
|Android component|Deferrable task method|Persistent method|
|Tasks running in broadcast receiver still in main thread and can run even app has been closed|`AlarmManager` give a way to perform time-based operations outside the lifetime of your application.|Work manager start persistent tasks and don't need to care about API level and play services|
|We need to spawn a new thread to run background task|Can run tasks in specific time by set alarm to wake up device and start a broadcast receiver|![](https://miro.medium.com/max/1400/1*ExahNy8HYsdp1NCiXyeERQ.png)|
|Hosting receiver process can be killed by system so we need to tell system to keep our receiver by using `goAsync()`|


## Foreground services

Foreground services perform operations that are noticeable to the user.


<!-- ## Background thread

Recommend Coroutine for kotlin app.

**Create thread pool:**
```kotlin
class MyApplication : Application() {
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)
}
```

**Executing in a background thread:**

```kotlin
sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class LoginRepository(private val responseParser: LoginResponseParser) {
    private const val loginUrl = "https://example.com/login"

    // Function that makes the network request, blocking the current thread
    fun makeLoginRequest(
        jsonBody: String
    ): Result<LoginResponse> {
        val url = URL(loginUrl)
        (url.openConnection() as? HttpURLConnection)?.run {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(jsonBody.toByteArray())

            return Result.Success(responseParser.parse(inputStream))
        }
        return Result.Error(Exception("Cannot open HttpURLConnection"))
    }
}
```
```kotlin
class LoginRepository(
    private val responseParser: LoginResponseParser
    private val executor: Executor
) {

    fun makeLoginRequest(jsonBody: String) {
        executor.execute {
            val ignoredResponse = makeSynchronousLoginRequest(url, jsonBody)
        }
    }

    private fun makeSynchronousLoginRequest(
        jsonBody: String
    ): Result<LoginResponse> {
        ... // HttpURLConnection logic
    }
}
```

**Communicating with the main thread**
```kotlin
class LoginRepository(
    private val responseParser: LoginResponseParser
    private val executor: Executor
) {

    fun makeLoginRequest(
        jsonBody: String,
        callback: (Result<LoginResponse>) -> Unit
    ) {
        executor.execute {
            try {
                val response = makeSynchronousLoginRequest(jsonBody)
                callback(response)
            } catch (e: Exception) {
                val errorResult = Result.Error(e)
                callback(errorResult)
            }
        }
    }
    ...
}
```

```kotlin
class LoginViewModel(
    private val loginRepository: LoginRepository
) {
    fun makeLoginRequest(username: String, token: String) {
        val jsonBody = "{ username: \"$username\", token: \"$token\"}"
        loginRepository.makeLoginRequest(jsonBody) { result ->
            when(result) {
                is Result.Success<LoginResponse> -> // Happy path
                else -> // Show error in UI
            }
        }
    }
}
```

**Using handler:**

- You can use a Handler to enqueue an action to be performed on a different thread.
- To specify the thread on which to run the action, construct the Handler using a Looper for the thread. A Looper is an object that runs the message loop for an associated thread. 

```kotlin
class MyApplication : Application() {
    val executorService: ExecutorService = Executors.newFixedThreadPool(4)
    val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
}
```
```
class LoginRepository(
    ...
    private val resultHandler: Handler
) {

    fun makeLoginRequest(
        jsonBody: String,
        callback: (Result<LoginResponse>) -> Unit
    ) {
          executor.execute {
              try {
                  val response = makeSynchronousLoginRequest(jsonBody)
                  resultHandler.post { callback(response) }
              } catch (e: Exception) {
                  val errorResult = Result.Error(e)
                  resultHandler.post { callback(errorResult) }
              }
          }
    }
    ...
}
```

```kotlin
class LoginRepository(...) {
    ...
    fun makeLoginRequest(
        jsonBody: String,
        resultHandler: Handler,
        callback: (Result<LoginResponse>) -> Unit
    ) {
        executor.execute {
            try {
                val response = makeSynchronousLoginRequest(jsonBody)
                resultHandler.post { callback(response) }
            } catch (e: Exception) {
                val errorResult = Result.Error(e)
                resultHandler.post { callback(errorResult) }
            }
        }
    }
}
```
**Configuring a thread pool**

- Initial and maximum pool size.
- Keep alive time and time unit. 
- An input queue that holds Runnable tasks.

```kotlin
class MyApplication : Application() {
    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    // Instantiates the queue of Runnables as a LinkedBlockingQueue
    private val workQueue: BlockingQueue<Runnable> =
            LinkedBlockingQueue<Runnable>()

    // Sets the amount of time an idle thread waits before terminating
    private const val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            NUMBER_OF_CORES,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue
    )
}
``` -->