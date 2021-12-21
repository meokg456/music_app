# Intent

Intent is a messaging object to request an action from another component.

There are 3 fundamental use cases:

- Starting an activity: 
    - We can start an activity by passing an `Intent` into `startActivity()`. 
    - The intent describes the activity to start and carries any necessary data. 
    - To return data from an activity when it finishes, call `startActivityForResult()`. Our activity receives the result as a separate `Intent` object in your activity's `onActivityResult()` callback
- Starting a service:
    - Android version > 5.0, we can use `JobScheduler` to start a service.
    - Android version < 5.0, we can start a service by using method of the `Service` class. We can start a service to perform a one-time operation (such as downloading a file) by passing an `Intent` to `startService()`
    - If the service is designed with a client-server interface, you can bind to the service from another component by passing an Intent to bindService().
- Delivering a broadcast:
    - You can deliver a broadcast to other apps by passing an Intent to `sendBroadcast()` or `sendOrderedBroadcast()`.

There are 2 intent types:

 - Explicit intents
 - Implicit intents
 
 Note:
- Don't use implicit intent for services.

## Build an Intent

An `Intent` object carries information that the Android system uses to determine which component to start

The primary information contained in an `Intent` is the following:


- **Component name**

    The name of the component to start.

    This is optional. If we give component name to intent the intent will be explicit if not the intent is implicit.



- **Action**

    A string naming the action to perform. Usually one of platform-defined values such as `ACTION_SEND` or `ACTION_VIEW`

    Specifing in `<action>` element.

    We can define our own actions and the action name require our app's package name prefix

    ```kotlin
    const val ACTION_TIMETRAVEL = "com.example.action.TIMETRAVEL"
    ```

- **Data**

    A description of the data associated with the intent.

    Specifing in `<data>` element.

- **Category**

    A string containing additional information about the kind of component that should handle the intent.

    Here are some common categories:
    - `CATEGORY_BROWSABLE`

    - `CATEGORY_LAUNCHER`

- **Extras**

    Key-value pairs that carry additional information required to accomplish the requested action.

    The Intent class specifies many EXTRA_* constants for standardized data types. For example, when creating an intent to send an email with `ACTION_SEND`, you can specify the to recipient with the `EXTRA_EMAIL` key, and specify the subject with the `EXTRA_SUBJECT` key.

    If we want to create our own key, be sure to include your app's package name as a prefix:
    ```kotlin
    const val EXTRA_GIGAWATTS = "com.example.EXTRA_GIGAWATTS"
    ```
- **Flags**

    Flags are defined in the Intent class that function as metadata for the intent.

    The flags may instruct the Android system how to launch an activity and how to treat it after it's launched.
### **Forcing an app chooser**

```kotlin
Intent.createChooser(sendIntent)
// Verify the original intent will resolve to at least one activity
if (sendIntent.resolveActivity(packageManager) != null) {
    startActivity(chooser)
}
```

### **Detect unsafe intent launches**

- Unsafe intent: Nested Intent

### Check for unsafe intent launches

- Use `detectUnsafeIntentLaunch()` when you configure your VmPolicy

```kotlin
fun onCreate() {
    StrictMode.setVmPolicy(VmPolicy.Builder()
        // Other StrictMode checks that you've previously added.
        // ...
        .detectUnsafeIntentLaunch()
        .penaltyLog()
        // Consider also adding penaltyDeath()
        .build())
}
```

### **Use intents more responsibly**

- Copy only the essential extras within intents, and perform any necessary sanitation and validation. 

- Don't export your app's components unnecessarily.

- Use a `PendingIntent` instead of a nested intent. 

# Intent filter

Declared by `<intent-filter>` element in our manifest file.

Each intent filter specifies the type of intents it accepts based on the intent's action, data, and category.

## Receiving an implicit intent

Inside the <intent-filter>, you can specify the type of intents to accept using one or more of these three elements:

`<action>` 

Declares the intent action accepted, in the name attribute. The value must be the literal string value of an action, not the class constant.

`<data>`

Declares the type of data accepted, using one or more attributes that specify various aspects of the data URI (scheme, host, port, path) and MIME type.

`<category>`

Declares the intent category accepted, in the name attribute. The value must be the literal string value of an action, not the class constant.

For example, here's an activity declaration with an intent filter to receive an ACTION_SEND intent when the data type is text:

```kotlin
<activity android:name="ShareActivity" android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain"/>
    </intent-filter>
</activity>
```

## Using a pending intent

A `PendingIntent` object is a wrapper around an Intent object.

The primary purpose of a `PendingIntent` is to grant permission to a foreign application to use the contained `Intent` as if it were executed from your app's own process.

Major use cases for a pending intent include the following:
- Declaring an intent to be executed when the user performs an action with your Notification
- Declaring an intent to be executed when the user performs an action with your App Widget
- Declaring an intent to be executed at a specified future time

When using `PendingIntent` our app doesn't execute intent by calling `startActivity()`. We must use:
- `PendingIntent.getActivity()` for an `Intent` that starts an `Activity`.
- `PendingIntent.getService()` for an `Intent` that starts a `Service`.
- `PendingIntent.getBroadcast()` for an `Intent` that starts a `BroadcastReceiver`.

### **Specify mutability**

If your app targets Android 12 or higher, you must specify the mutability of each `PendingIntent` object that your app creates.

To declare that a given `PendingIntent` object is mutable or immutable, use the `PendingIntent.FLAG_MUTABLE` or `PendingIntent.FLAG_IMMUTABLE flag`.

If your app attempts to create a `PendingIntent` object without setting either mutability flag, the system throws an `IllegalArgumentException`.

### **Create immutable pending intents whenever possible**

```kotlin
val pendingIntent = PendingIntent.getActivity(applicationContext,
        REQUEST_CODE, intent,
        /* flags */ PendingIntent.FLAG_IMMUTABLE)
```

However, certain use cases require mutable PendingIntent objects instead:

- Supporting direct reply actions in notifications. Usually, you request this change by passing `FILL_IN_CLIP_DATA` as a flag to the `fillIn()` method.
- Associating notifications with the Android Auto framework, using instances of `CarAppExtender`.
- Placing conversations in bubbles using instances of `PendingIntent`.
- Requesting device location information by calling `requestLocationUpdates()` or similar APIs.
- Scheduling alarms using `AlarmManager`. The mutable `PendingIntent` object allows the system to add the `EXTRA_ALARM_COUNT` intent extra.

Note: If your app creates a mutable PendingIntent object, it's strongly recommended that you use an explicit intent and fill in the `ComponentName`.

### **Use explicit intents within pending intents**

To better define how other apps can use your app's pending intents, always wrap a pending intent around an explicit intent. To help follow this best practice, do the following:
- Check that the action, package, and component fields of the base intent are set.
- Use FLAG_IMMUTABLE, added in Android 6.0 (API level 23), to create pending intents.
