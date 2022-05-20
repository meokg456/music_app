# Overview

A unit test verifies the behavior of a small section of code, the unit under test. It does so by executing that code and checking the result.

A local test runs directly on your own workstation, rather than an Android device or emulator.

## Local tests location

By default, the source files for local unit tests are placed in module-name/src/test/

## Adding testing dependencies
```kotlin
dependencies {
  // Required -- JUnit 4 framework
  testImplementation "junit:junit:$jUnitVersion"
  // Optional -- Robolectric environment
  testImplementation "androidx.test:core:$androidXTestVersion"
  // Optional -- Mockito framework
  testImplementation "org.mockito:mockito-core:$mockitoVersion"
  // Optional -- mockito-kotlin
  testImplementation "org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion"
  // Optional -- Mockk framework
  testImplementation "io.mockk:mockk:$mockkVersion"
}
```

## Create a local unit test class

```kotlin
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {
  @Test fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
    assertTrue(EmailValidator.isValidEmail("name@email.com"))
  }

}

```
## Mocking Android dependencies

```kotlin
import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private const val FAKE_STRING = "HELLO WORLD"

@RunWith(MockitoJUnitRunner::class)
class MockedContextTest {

  @Mock
  private lateinit var mockContext: Context

  @Test
  fun readStringFromContext_LocalizedString() {
    // Given a mocked Context injected into the object under test...
    val mockContext = mock<Context> {
        on { getString(R.string.name_label) } doReturn "FAKE_STRING"
    }

    val myObjectUnderTest = ClassUnderTest(mockContext)

    // ...when the string is returned from the object under test...
    val result: String = myObjectUnderTest.getName()

    // ...then the result should be the expected one.
    assertEquals(result, FAKE_STRING)
  }
}
```
## Error: "Method ... not mocked"

The Mockable Android library throws an exception if you try to access any of its methods with the Error: "Method ... not mocked message.

If the exceptions thrown are problematic for your tests, you can change the behavior so that methods instead return either null or zero, depending on the return type. To do so, add the following configuration in your project's top-level `build.gradle` file in Groovy:

```kotlin
android {
  ...
  testOptions {
    unitTests.returnDefaultValues = true
  }
```