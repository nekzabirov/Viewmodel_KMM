package com.nekzabirov.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.UIKit.UIViewController
import kotlin.reflect.KClass

inline fun <reified VM : ViewModel> UIViewController.viewModel(crossinline factor: () -> VM): VM {
    val viewModelProvider = ViewModelProvider<VM>(
        factory = object : ViewModelProvider.Factory<VM> {
            override fun create(modelClass: KClass<VM>): VM = factor()
        }
    )

    return viewModelProvider.get(VM::class)
}

@Composable
actual inline fun <reified VM : ViewModel> viewModel(crossinline factory: @DisallowComposableCalls () -> VM): VM {
    val key = remember { VM::class.qualifiedName ?: VM::class.toString() }

    val viewModelFactory = remember { object : ViewModelProvider.Factory<VM> {
        override fun create(modelClass: KClass<VM>): VM = factory()
    } }

    val viewModelProvider = remember { ViewModelProvider(viewModelFactory) }

    return remember {
        viewModelProvider.get(VM::class, key)
    }
}