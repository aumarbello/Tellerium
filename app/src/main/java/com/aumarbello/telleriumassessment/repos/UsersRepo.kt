package com.aumarbello.telleriumassessment.repos

import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.db.UsersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepo @Inject constructor (private val dao: UsersDao) {
    suspend fun loadUsers() = withContext(Dispatchers.IO) {
        dao.fetchUsers()
    }

    suspend fun getUser(id: String) = withContext(Dispatchers.IO) {
        dao.findById(id)
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateUser(user)
    }
}