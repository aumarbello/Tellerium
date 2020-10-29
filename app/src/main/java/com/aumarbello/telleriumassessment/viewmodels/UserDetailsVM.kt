package com.aumarbello.telleriumassessment.viewmodels

import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.repos.UsersRepo
import javax.inject.Inject

class UserDetailsVM @Inject constructor(private val repo: UsersRepo): BaseVM<UserEntity>() {
    fun getUser(userId: String) {
        loadData("An error occurred") { repo.getUser(userId) }
    }

    fun updateUser(user: UserEntity) {
        load("An error occurred") { repo.updateUser(user) }
    }
}