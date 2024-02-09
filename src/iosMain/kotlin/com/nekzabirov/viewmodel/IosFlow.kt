package com.nekzabirov.viewmodel

import com.nekzabirov.viewmodel.unit.Closeable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class IosFlow<T>(private val origin: Flow<T>,
                 private val dispatcher: CoroutineDispatcher) : Flow<T> by origin {
    fun watch(block: (T) -> Unit): Closeable {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(dispatcher + job))

        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}

fun <T> Flow<T>.toSwift() = IosFlow(this, Dispatchers.IO)