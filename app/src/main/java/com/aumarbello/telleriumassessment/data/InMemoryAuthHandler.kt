package com.aumarbello.telleriumassessment.data

import android.content.Context

/**
 * Simple in-memory authenticator that validates user credentials against
 * a list of valid test credentials
 */
class InMemoryAuthHandler (context: Context): AuthHandler {
    private val preferences = context.getSharedPreferences("tellerium", Context.MODE_PRIVATE)

    private val validCredentials = listOf(
        "test@tellerium.io" to "password"
    )

    override suspend fun authenticate(email: String, password: String): Boolean {
        val isValid = validCredentials.contains(email to password)
        if (isValid) {
            preferences.edit().putBoolean(loggedIn, true).apply()
        }

        return isValid
    }

    override fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean(loggedIn, false)
    }

    override fun logOut() {
        preferences.edit().putBoolean(loggedIn, false).apply()
    }

    private companion object {
        const val loggedIn = "userLoggedIn"
    }
}