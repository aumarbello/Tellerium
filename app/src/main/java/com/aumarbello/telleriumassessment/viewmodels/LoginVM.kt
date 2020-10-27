package com.aumarbello.telleriumassessment.viewmodels

import com.aumarbello.telleriumassessment.OpenForTesting
import com.aumarbello.telleriumassessment.repos.LoginRepo
import javax.inject.Inject

@OpenForTesting
class LoginVM @Inject constructor(private val repo: LoginRepo): BaseVM<Boolean>() {
    fun login(email: String, password: String) {
        loadData("Unable to login") { repo.login(email, password) }
    }
}