package com.nekzabirov.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class ViewModel: CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob()

    open fun onClear() {

    }

    fun clear() {
        coroutineContext.cancel()
        onClear()
    }
}

@Composable
expect inline fun <reified VM: ViewModel> viewModel(crossinline factory: @DisallowComposableCalls () -> VM): VM