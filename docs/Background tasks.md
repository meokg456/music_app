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

