# Overview

- The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way. 

- Benefits:
    - The ViewModel class allows data to survive configuration changes such as screen rotations.
    - Reduce resources usages
    - Easier to test

# Implement a ViewModel

```kotlin
class MyViewModel : ViewModel() {
    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}
```

```kotlin
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.

        // Use the 'by viewModels()' Kotlin property delegate
        // from the activity-ktx artifact
        val model: MyViewModel by viewModels()
        model.getUsers().observe(this, Observer<List<User>>{ users ->
            // update UI
        })
    }
}
```

# The lifecycle of a ViewModel

![](https://developer.android.com/images/topic/libraries/architecture/viewmodel-lifecycle.png)

# Share data between fragments
```kotlin
class SharedViewModel : ViewModel() {
    val selected = MutableLiveData<Item>()

    fun select(item: Item) {
        selected.value = item
    }
}

class ListFragment : Fragment() {

    private lateinit var itemSelector: Selector

    // Use the 'by activityViewModels()' Kotlin property delegate
    // from the fragment-ktx artifact
    private val model: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemSelector.setOnClickListener { item ->
            // Update the UI
        }
    }
}

class DetailFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate
    // from the fragment-ktx artifact
    private val model: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.selected.observe(viewLifecycleOwner, Observer<Item> { item ->
            // Update the UI
        })
    }
}
```

# ViewModelFactory

- ViewModelFactory is used to create ViewModel with arguments
Implementation:

```kotlin

class CountViewModel(start: Int) : ViewModel() {
    var count = MutableLiveData(start)
    fun increase() {
        count.value = count.value?.plus(1)
    }
}

class CountViewModelFactory constructor(private val start: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountViewModel::class.java)) {
            return CountViewModel(start) as T
        }
        throw IllegalArgumentException("ViewModel not found")

    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener {
            countViewModel.increase()
        }

        countViewModelFactory = CountViewModelFactory(10)
        countViewModel = ViewModelProvider(this, countViewModelFactory)[CountViewModel::class.java]
        binding.viewModel = countViewModel
        val countObserver = Observer<Int> {
            binding.fab.text = it.toString()
        }

        countViewModel.count.observe(this, countObserver)
    }

```