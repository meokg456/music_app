# View Binding

View binding:

- View binding is a feature that allows us to more easily write code that interacts with views.

- In most cases, view binding will replace `findViewById`

- Once view binding is enabled in a module, it generates a binding class for each XML layout file present in that module.

Benefits of view binding:

- **Null safety**: Since view binding creates direct references to views, there's no risk of a null pointer exception due to an invalid view ID.

- **Type safety**: The fields in each binding class have types matching the views they reference in the XML file. This means that there's no risk of a class cast exception.

## Setup instructions

To enable view binding, we need to set the viewBinding build option to true in the module-level build.gradle file:

```kotlin
android {
    ...
    buildFeatures {
        viewBinding = true
    }
}
```

## View binding in Activities

```xml
<LinearLayout ... >
    <TextView android:id="@+id/name" />
    <ImageView android:cropToPadding="true" />
    <Button android:id="@+id/button"
        android:background="@drawable/rounded_button" />
</LinearLayout>
```

```kotlin
private lateinit var binding: ResultProfileBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ResultProfileBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)
}
```

```kotlin
binding.name.text = viewModel.name
binding.button.setOnClickListener { viewModel.userClicked() }
```

## View binding in fragments

```kotlin
private var _binding: ResultProfileBinding? = null
// This property is only valid between onCreateView and
// onDestroyView.
private val binding get() = _binding!!

override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    _binding = ResultProfileBinding.inflate(inflater, container, false)
    val view = binding.root
    return view
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

