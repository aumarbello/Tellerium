package com.aumarbello.telleriumassessment.utils

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsErrorDisplayedMatcher: TypeSafeMatcher<View> {
    private var errorText: Any? = null

    constructor(): super()

    constructor (errorText: String): super() {
        this.errorText = errorText
    }
    constructor(errorText: Int): super() {
        this.errorText = errorText
    }

    override fun describeTo(description: Description?) {
        description?.appendText("has visible error message")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item is TextInputLayout && item.isErrorEnabled  && textMatches(item.error, item.context)
    }

    private fun textMatches(displayedError: CharSequence?, context: Context): Boolean {
        return when(errorText) {
            is Int -> {
                val text = context.getString(errorText as Int)
                displayedError == text
            }
            is String -> displayedError == errorText

            else -> displayedError != null
        }
    }

    companion object {
        fun isErrorDisplayed() = IsErrorDisplayedMatcher()
        fun isErrorDisplayed(errorText: String) = IsErrorDisplayedMatcher(errorText)
        fun isErrorDisplayed(@StringRes errorText: Int) = IsErrorDisplayedMatcher(errorText)
    }
}