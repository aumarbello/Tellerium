package com.aumarbello.telleriumassessment.data

interface AuthHandler {
    suspend fun authenticate(email: String, password: String): Boolean
    fun isUserLoggedIn(): Boolean
    fun logOut()
}