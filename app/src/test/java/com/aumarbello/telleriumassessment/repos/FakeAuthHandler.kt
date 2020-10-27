package com.aumarbello.telleriumassessment.repos

import com.aumarbello.telleriumassessment.data.AuthHandler

class FakeAuthHandler (
    private val testEmail: String = "test@tellerium.com",
    private val testPassword: String = "password"
): AuthHandler {
    override suspend fun authenticate(email: String, password: String): Boolean {
        return email == testEmail && password == testPassword
    }

    override fun isUserLoggedIn() = false

    override fun logOut() {}
}