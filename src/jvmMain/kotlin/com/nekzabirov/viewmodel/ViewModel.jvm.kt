package com.nekzabirov.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
actual inline fun <reified VM : ViewModel> viewModel(crossinline factory: @DisallowComposableCalls () -> VM): VM {
    val key = remember { VM::class.java.canonicalName ?: VM::class.toString() }

    val viewModelFactory = remember { NewInstanceFactory<VM>() }

    val viewModelProvider = remember { ViewModelProvider(viewModelFactory) }

    DisposableEffect(Unit) {
        onDispose { viewModelProvider.clear(key) }
    }

    return remember {
        viewModelProvider.get(VM::class, key)
    }
}