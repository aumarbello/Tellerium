package com.aumarbello.telleriumassessment.utils

import androidx.lifecycle.ViewModel
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import javax.inject.Provider

/**
 * Creates a one off view model factory for the given view model instance.
 */
object ViewModelUtil {
    fun <T : ViewModel> createFor(vararg viewModels: T): ViewModelFactory {
        val viewModelMap = mutableMapOf<Class<out ViewModel>, Provider<ViewModel>>()
        viewModels.forEach {
            viewModelMap[it::class.java] = providerOf(it)
        }
        return ViewModelFactory(viewModelMap.toMap())
    }

    private fun <T : ViewModel> providerOf(model: T): Provider<ViewModel> {
        return Provider { model }
    }
}