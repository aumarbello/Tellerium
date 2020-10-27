package com.aumarbello.telleriumassessment.models

import com.aumarbello.telleriumassessment.utils.Event

data class State <T> (
    val loading: Boolean = false,
    val error: Event<String>? = null,
    val data: T? = null
)