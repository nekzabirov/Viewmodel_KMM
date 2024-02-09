package com.nekzabirov.viewmodel

import kotlin.reflect.KClass

open class ViewModelProvider<T : ViewModel>(private val factory: Factory<T>) {
    interface Factory<T : ViewModel> {
        fun create(modelClass: KClass<T>): T
    }

    fun get(modelKClass: KClass<T>): T {
        val key: String = modelKClass::qualifiedName.invoke() ?: modelKClass.toString()

        return get(modelKClass, key)
    }

    fun get(modelKClass: KClass<T>, key: String): T =
        ViewModelStore[key] as? T
            ?: factory.create(modelKClass).also {
                ViewModelStore.put(key, it)
            }

    fun clear(key: String) {
        ViewModelStore.remove(key)
    }
}