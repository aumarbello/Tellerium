package com.aumarbello.telleriumassessment.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(vararg users: UserEntity)

    @Transaction
    fun saveUsers(users: List<UserEntity>) {
        deleteAllUsers()

        insertOrUpdateUser(*users.toTypedArray())
    }

    @Query("SELECT * FROM users")
    fun fetchUsers(): LiveData<List<UserEntity>>

    @Query("DELETE FROM users")
    fun deleteAllUsers()
}