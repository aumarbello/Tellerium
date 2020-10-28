package com.aumarbello.telleriumassessment.viewmodels

import androidx.lifecycle.*
import com.aumarbello.telleriumassessment.OpenForTesting
import com.aumarbello.telleriumassessment.models.State
import com.aumarbello.telleriumassessment.utils.Event
import kotlinx.coroutines.launch

@OpenForTesting
abstract class BaseVM <T>: ViewModel() {
    private val _state = MediatorLiveData<State<T>>()
    val state: LiveData<State<T>> = _state

    protected fun loadData(message: String, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                _state.value = State(true)

                updateState { it.copy(data = block()) }
            } catch (ex: Throwable) {
                ex.printStackTrace()

                updateState { it.copy(error = Event(resolveErrorMessage(ex) ?: message)) }
            } finally {
                updateState { it.copy(loading = false) }
            }
        }
    }

    protected fun observeData(message: String, block: suspend () -> LiveData<T>) {
        viewModelScope.launch {
            try {
                _state.value = State(true)

                _state.addSource(block()) {
                    val currentValue = _state.value ?: return@addSource
                    _state.value = currentValue.copy(data = it)
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()

                updateState { it.copy(error = Event(resolveErrorMessage(ex) ?: message)) }
            } finally {
                updateState { it.copy(loading = false) }
            }
        }
    }

    private suspend fun updateState(update: suspend (state: State<T>) -> State<T>) {
        val currentState = _state.value ?: State()
        _state.value = update(currentState)
    }

    private fun resolveErrorMessage(throwable: Throwable): String? {
        return if (throwable.message.isNullOrEmpty()) {
            null
        } else {
            throwable.message
        }
    }
}