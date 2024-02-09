package com.nekzabirov.viewmodel

import java.lang.RuntimeException
import kotlin.reflect.KClass

open class NewInstanceFactory<VM : ViewModel> : ViewModelProvider.Factory<VM> {
    override fun create(modelClass: KClass<VM>): VM {
        return try {
            modelClass.java.getDeclaredConstructor().newInstance()
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}