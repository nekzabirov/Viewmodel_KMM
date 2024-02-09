package com.nekzabirov.viewmodel

import android.app.Application
import kotlin.reflect.KClass

class AndroidFactory<VM : ViewModel>(private val application: Application): NewInstanceFactory<VM>() {
    override fun create(modelClass: KClass<VM>): VM {
        return modelClass.java.getDeclaredConstructor().let { constructor ->
            val params = constructor.parameters
            if (params.size == 1 && constructor.parameters.first().type.isInstance(application)) {
                constructor.newInstance(application)
            } else {
                super.create(modelClass)
            }
        }
    }
}