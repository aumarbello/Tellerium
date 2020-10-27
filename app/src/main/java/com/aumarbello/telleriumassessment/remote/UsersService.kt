package com.aumarbello.telleriumassessment.remote

import com.aumarbello.telleriumassessment.models.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersService {
    @GET("get-sample-farmers")
    suspend fun loadUsers(@Query("limit") limit: Int): UserResponse
}