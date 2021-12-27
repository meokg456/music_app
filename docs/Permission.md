# Permisions on Android

- Restricted data
- Restricted actions

## Workflow for using permissions

![](https://developer.android.com/images/training/permissions/workflow-overview.svg)

## Type of permissions

|Install-time permissions|Run-time permissions|Special permissions|
|------------------------|--------------------|-------------------|
|Automaticly granted when user installs app. |Dangerous permissions | Permission to protect powerful actions |
|App store presents an install-time permission notice to the user in app's detail. |Need to request to the user. |  Only the platform and OEMs can define special permissions  |
| ![](https://developer.android.com/images/training/permissions/install-time.svg) | ![](https://developer.android.com/images/training/permissions/runtime.svg)  | Drawing over other apps |
|Normall permission: Permission with normal protection level|||
|Signature permissions |||

## Declaring permissions

### **Declaring permissions might need to request**

```xml
<manifest ...>
    <uses-permission android:name="android.permission.CAMERA"/>
    <application ...>
        ...
    </application>
</manifest>
```

### **Declaring hardware**

```xml
<manifest ...>
    <application>
        ...
    </application>
    <uses-feature android:name="android.hardware.camera"
                  android:required="false" />
<manifest>
```

### **Determine hardware availability**

```kotlin
// Check whether your app is running on a device that has a front-facing camera.
if (applicationContext.packageManager.hasSystemFeature(
        PackageManager.FEATURE_CAMERA_FRONT)) {
    // Continue with the part of your app's workflow that requires a
    // front-facing camera.
} else {
    // Gracefully degrade your app experience.
}
```