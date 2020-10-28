package com.aumarbello.telleriumassessment.models

import com.google.gson.annotations.SerializedName

data class UserResponse (
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("data")
    val data: Response
)

data class Response (
    @SerializedName("farmers")
    val users: List<User>,
    @SerializedName("totalRec")
    val total: Int,
    @SerializedName("imageBaseUrl")
    val baseImageUrl: String
)

data class User (
    @SerializedName("farmer_id")
    val id: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("surname")
    val lastName: String,
    @SerializedName("middle_name")
    val middleName: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("passport_photo")
    val imagePath: String,
    @SerializedName("marital_status")
    val maritalStatus: String,
    @SerializedName("dob")
    val dateOfBirth: String,
    @SerializedName("reg_no")
    val registrationNumber: String,
    @SerializedName("mobile_no")
    val mobileNumber: String,
    @SerializedName("email_address")
    val emailAddress: String
)