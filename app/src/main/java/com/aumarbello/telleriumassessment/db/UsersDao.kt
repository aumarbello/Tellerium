package com.aumarbello.telleriumassessment.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateUser(vararg users: UserEntity)

    @Update
    fun updateUser(user: UserEntity)

    @Transaction
    fun saveUsers(users: List<UserEntity>) {
        deleteAllUsers()

        insertOrUpdateUser(*users.toTypedArray())
    }

    @Query("SELECT * FROM users")
    fun fetchUsers(): LiveData<List<UserEntity>>

    @Query("SELECT COUNT(*)  FROM users")
    suspend fun getUsersCount(): Int

    @Query("SELECT * FROM users where id = :userId LIMIT 1")
    fun findById(userId: String): UserEntity

    @Query("DELETE FROM users")
    fun deleteAllUsers()
}