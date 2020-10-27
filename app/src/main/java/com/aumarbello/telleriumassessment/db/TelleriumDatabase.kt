package com.aumarbello.telleriumassessment.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aumarbello.telleriumassessment.utils.DatabaseConstants

@Database(entities = [UserEntity::class], version = DatabaseConstants.Version, exportSchema = false)
@TypeConverters(UserLocationConverter::class, CoordinatesConverter::class)
abstract class TelleriumDatabase:  RoomDatabase() {
    abstract fun usersDao(): UsersDao

    companion object {
        private lateinit var database: TelleriumDatabase

        fun getInstance(context: Context): TelleriumDatabase {
            if (!::database.isInitialized) {
                synchronized(this) {
                    database = Room.databaseBuilder(
                        context,
                        TelleriumDatabase::class.java,
                        DatabaseConstants.DatabaseName
                    ).build()
                }
            }

            return database
        }
    }
}