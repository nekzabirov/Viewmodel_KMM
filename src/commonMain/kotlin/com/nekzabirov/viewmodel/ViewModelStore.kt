package com.nekzabirov.viewmodel

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

internal object ViewModelStore {
    @OptIn(InternalCoroutinesApi::class)
    private val lock = SynchronizedObject()

    private val map = mutableMapOf<String, ViewModel>()

    @OptIn(InternalCoroutinesApi::class)
    fun put(key: String, viewModel: ViewModel) = synchronized(lock) {
        val oldViewModel = map.put(key, viewModel)
        oldViewModel?.clear()
    }

    @OptIn(InternalCoroutinesApi::class)
    fun remove(key: String) = synchronized(lock) {
        val vm = map.remove(key)
        vm?.clear()
    }

    @OptIn(InternalCoroutinesApi::class)
    operator fun get(key: String): ViewModel? = synchronized(lock) {
        return map[key]
    }

    fun keys(): Set<String> {
        return HashSet(map.keys)
    }

    /**
     * Clears internal storage and notifies `ViewModel`s that they are no longer used.
     */
    @OptIn(InternalCoroutinesApi::class)
    fun clear() = synchronized(lock) {
        for (vm in map.values) {
            vm.clear()
        }
        map.clear()
    }
}