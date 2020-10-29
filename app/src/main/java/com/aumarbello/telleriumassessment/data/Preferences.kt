package com.aumarbello.telleriumassessment.data

interface Preferences {
    suspend fun authenticate(email: String, password: String): Boolean
    fun isUserLoggedIn(): Boolean
    fun logOut()
    fun setUsersCount(count: Int)
    fun usersCount(): Int
    fun setTotalUsers(total: Int)
    fun getTotalUsers(): Int
}