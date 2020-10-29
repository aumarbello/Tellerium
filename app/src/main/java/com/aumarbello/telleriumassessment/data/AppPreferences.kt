package com.aumarbello.telleriumassessment.data

import android.content.Context

/**
 * Simple in-memory authenticator that validates user credentials against
 * a list of valid test credentials
 */
class AppPreferences (context: Context): Preferences {
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

    override fun setUsersCount(count: Int) {
        preferences.edit().putInt(userCount, count).apply()
    }

    override fun usersCount(): Int {
        return preferences.getInt(userCount, 10)
    }

    override fun setTotalUsers(total: Int) {
        preferences.edit().putInt(totalUsers, total).apply()
    }

    override fun getTotalUsers(): Int {
        return preferences.getInt(totalUsers, 0)
    }

    private companion object {
        const val loggedIn = "userLoggedIn"
        const val userCount = "usersCount"
        const val totalUsers = "totalUsersCount"
    }
}