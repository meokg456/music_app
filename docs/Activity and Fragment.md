# Activity

## Concept

- Activity is an Android component which provide a windows to show the UI.

- Main activity will be the activity that will show up when user launch the app.

- Each activity can start another activity in order to perform different action.

## Configuring the manifest

- Activity is declared by \<activity> element and it is under \<application> element

- The only required attribute for this element is android:name, which specifies the class name of the activity.

```xml
<manifest ... >
  <application ... >
      <activity android:name=".ExampleActivity" />
      ...
  </application ... >
  ...
</manifest >
```

## Declare intent filters

- Intent filters are a very powerful feature of the Android platform.

- They provide the ability to launch an activity based not only on an explicit request, but also an implicit one.

- When an implicit request is performed, the system will know which activity should be launch base on its intent filter. But in many case, many activities adapt that request and can be launch. So now the user need to choose one of them to be launch

## Declare permissions

- A parent activity cannot launch a child activity unless both activities have the same permissions in their manifest

- Use /<uses-permission> to declare app's permissions

- Use `android:permission` attribute to declare activity's permission
```xml
<manifest>
<activity android:name="...."
   android:permission=”com.google.socialapp.permission.SHARE_POST”

/>
```
```xml
<manifest>
   <uses-permission android:name="com.google.socialapp.permission.SHARE_POST" />
</manifest>
```

## Managing the activity lifecycle

- `onCreate()`: this callback will called when system create the activity

- `onStart()`: This callback contains what amounts to the activity’s final preparations for coming to the foreground and becoming interactive.

- `onResume()`: The system invokes this callback just before the activity starts interacting with the user. The `onPause()` callback always follows `onResume()`.

- `onPause()`: The system calls `onPause()` when the activity loses focus and enters a Paused state.

- `onStop()`: The system calls onStop() when the activity is no longer visible to the user.

- `onRestart()`: The system invokes this callback when an activity in the Stopped state is about to restart.

- `onDestroy()`: The system invokes this callback before an activity is destroyed.

    ![](https://developer.android.com/guide/components/images/activity_lifecycle.png)

# Fragment

- A Fragment represents a reusable portion of your app's UI.

- Fragments cannot live on their own--they must be hosted by an activity or another fragment.

## Modularity

- Fragments introduce modularity and reusability into your activity’s UI by allowing you to divide the UI into discrete chunks.

![](https://developer.android.com/images/guide/fragments/fragment-screen-sizes.png)

## Create a fragment

### **Setup your environment**

- Fragments require a dependency on the AndroidX Fragment library. You need to add the Google Maven repository to your project's build.gradle file in order to include this dependency.

```kotlin
buildscript {
    ...

    repositories {
        google()
        ...
    }
}

allprojects {
    repositories {
        google()
        ...
    }
}
```

- To include the AndroidX Fragment library to your project, add the following dependencies in your app's build.gradle file:

```kotlin
dependencies {
    val fragment_version = "1.4.0"

    // Java language implementation
    implementation("androidx.fragment:fragment:$fragment_version")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
}
```

### **Create fragment class**

**Add a fragment via XML**

```xml
<!-- res/layout/example_activity.xml -->
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.example.ExampleFragment" />
```

**Add a fragment programmatically**
```xml
<!-- res/layout/example_activity.xml -->
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```kotlin
class ExampleActivity : AppCompatActivity(R.layout.example_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) { //Ensure the fragment added once
            val bundle = bundleOf("some_int" to 0)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ExampleFragment>(R.id.fragment_container_view, , args = bundle)
            }
        }
    }
}
```
```kotlin
class ExampleFragment : Fragment(R.layout.example_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val someInt = requireArguments().getInt("some_int")
        ...
    }
}
```

## Fragment manager

- `FragmentManager` is the class responsible for performing actions on your app's fragments, such as adding, removing, or replacing them, and adding them to the back stack.

### **Access the FragmentManager**

**Accessing in an activity**

- Use `getSupportFragmentManager()` method

**Accessing in a Fragment**

- `FragmentManager` that manages the fragment's children through `getChildFragmentManager()`

 - If you need to access its host `FragmentManager`, you can use `getParentFragmentManager()`

 ![](https://developer.android.com/images/guide/fragments/manager-mappings.png)

 ## Using the FragmentManager
