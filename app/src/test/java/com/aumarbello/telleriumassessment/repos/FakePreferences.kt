package com.aumarbello.telleriumassessment.repos

import com.aumarbello.telleriumassessment.data.Preferences

class FakePreferences (
    private val testEmail: String = "test@tellerium.com",
    private val testPassword: String = "password"
): Preferences {
    override suspend fun authenticate(email: String, password: String): Boolean {
        return email == testEmail && password == testPassword
    }

    override fun isUserLoggedIn() = false

    override fun logOut() {}

    override fun setUsersCount(count: Int) {}

    override fun usersCount() = 0

    override fun setTotalUsers(total: Int) {}

    override fun getTotalUsers() = 0
}