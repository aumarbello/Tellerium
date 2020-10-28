package com.aumarbello.telleriumassessment.repos

import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.db.UsersDao
import com.aumarbello.telleriumassessment.remote.UsersService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepo @Inject constructor(
    private val service: UsersService,
    private val dao: UsersDao
) {
    suspend fun loadUsers(limit: Int) = withContext(Dispatchers.IO) {
        val response = service.loadUsers(limit)
        dao.saveUsers(response.data.users.map {
            UserEntity(
                it.id,
                it.firstName,
                "${it.middleName} ${it.lastName}",
                null,
                it.dateOfBirth,
                "${response.data.baseImageUrl}${it.imagePath}",
                it.mobileNumber,
                it.emailAddress,
                it.gender,
                null,
                emptyList(),
                it.address,
                it.city,
                it.state,
                it.maritalStatus,
                it.registrationNumber
            )
        })

        response.data.total
    }
}