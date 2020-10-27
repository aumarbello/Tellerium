package com.aumarbello.telleriumassessment.repos

import com.aumarbello.telleriumassessment.OpenForTesting
import com.aumarbello.telleriumassessment.data.AuthHandler
import javax.inject.Inject

@OpenForTesting
class LoginRepo @Inject constructor(private val authenticator: AuthHandler) {
    suspend fun login(email: String, password: String): Boolean {
        return if (authenticator.authenticate(email, password)) {
            true
        } else
            throw IllegalArgumentException("Invalid email/password provided")
    }
}