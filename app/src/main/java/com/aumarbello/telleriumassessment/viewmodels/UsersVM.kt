package com.aumarbello.telleriumassessment.viewmodels

import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.repos.UsersRepo
import javax.inject.Inject

class UsersVM @Inject constructor(private val repo: UsersRepo): BaseVM<List<UserEntity>>() {
    fun loadUsers() {
        observeData("An error occurred") { repo.loadUsers() }
    }
}