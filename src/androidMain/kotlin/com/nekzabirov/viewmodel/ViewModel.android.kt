package com.nekzabirov.viewmodel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.reflect.KClass

class ViewModelLazy<VM : ViewModel>(
    private val modelClass: KClass<VM>,
    private val key: String,
    private val viewModelProvider: ViewModelProvider<VM>
) : Lazy<VM> {
    private var cached: VM? = null

    override val value: VM
        get() {
            if (cached != null) {
                return cached!!
            }

            return viewModelProvider.get(modelClass, key)
        }

    override fun isInitialized(): Boolean = cached != null
}

inline fun <reified VM : ViewModel> ComponentActivity.viewModel(
    key: String = VM::class.qualifiedName ?: VM::class.toString(),
    viewModelProvider: ViewModelProvider<VM> = ViewModelProvider(AndroidFactory(application))
): Lazy<VM> {
    lifecycle.addObserver(observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            viewModelProvider.clear(key)
        }
    })

    return ViewModelLazy(
        modelClass = VM::class,
        key = key,
        viewModelProvider = viewModelProvider
    )
}

inline fun <reified VM : ViewModel> Fragment.viewModel(
    key: String = VM::class.qualifiedName ?: VM::class.toString(),
    viewModelProvider: ViewModelProvider<VM>? = null
): Lazy<VM> {
    val sViewModelProvider = viewModelProvider ?: ViewModelProvider(AndroidFactory<VM>(requireActivity().application))

    lifecycle.addObserver(observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            sViewModelProvider.clear(key)
        }
    })

    return ViewModelLazy(
        modelClass = VM::class,
        key = key,
        viewModelProvider = sViewModelProvider
    )
}

inline fun <reified VM : ViewModel> Fragment.sharedViewModel(
    key: String = VM::class.qualifiedName ?: VM::class.toString(),
    viewModelProvider: ViewModelProvider<VM>? = null
): Lazy<VM> {
    val sViewModelProvider = viewModelProvider ?: ViewModelProvider(AndroidFactory<VM>(requireActivity().application))

    return requireActivity().viewModel<VM>(
        key = key,
        viewModelProvider = sViewModelProvider
    )
}

@Composable
actual inline fun <reified VM : ViewModel> viewModel(crossinline factory: @DisallowComposableCalls () -> VM): VM {
    val context = LocalContext.current as ComponentActivity

    val key = remember { VM::class.java.canonicalName ?: VM::class.toString() }

    val viewModelFactory = remember { NewInstanceFactory<VM>() }

    val viewModelProvider = remember { ViewModelProvider(viewModelFactory) }

    context.lifecycle.addObserver(observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            viewModelProvider.clear(key)
        }
    })

    return remember {
        viewModelProvider.get(VM::class, key)
    }
}