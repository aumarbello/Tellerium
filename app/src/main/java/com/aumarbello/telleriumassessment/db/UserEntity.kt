package com.aumarbello.telleriumassessment.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aumarbello.telleriumassessment.models.UserLocation


@Entity (
    tableName = "users"
)
data class UserEntity (
    @PrimaryKey
    val id: String,

    val firstName: String,
    val lastName: String,
    val farmName: String?,
    val dateOfBirth: String,
    val imageUrl: String,
    val phoneNumber: String,
    val emailAddress: String,
    val gender: String,
    val location: UserLocation?,
    val farmCoordinates: List<UserLocation>,
    val address: String,
    val city: String,
    val state: String,
    val maritalStatus: String,
    val registrationNumber: String
)