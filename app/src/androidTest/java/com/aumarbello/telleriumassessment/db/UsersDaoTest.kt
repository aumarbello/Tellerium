package com.aumarbello.telleriumassessment.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aumarbello.telleriumassessment.getOrAwaitValue
import com.aumarbello.telleriumassessment.TestObjects
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersDaoTest {
    private lateinit var usersDao: UsersDao
    private lateinit var database: TelleriumDatabase

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TelleriumDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        usersDao = database.usersDao()
    }

    @Test
    fun writeUsersAndReadList() {
        usersDao.saveUsers(TestObjects.users)

        val users = usersDao.fetchUsers().getOrAwaitValue()

        assertThat(users, `is`(TestObjects.users))
    }

    @Test
    fun writeUsersAndReadUpdatedList() {
        usersDao.saveUsers(TestObjects.users)

        usersDao.insertOrUpdateUser(TestObjects.user)

        val users = usersDao.fetchUsers().getOrAwaitValue()
        val updatedUsers = TestObjects.users.toMutableList().apply {
            add(TestObjects.user)
        }.toList()

        assertThat(users, `is`(updatedUsers))
    }

    @Test
    fun whenUsersExistsDeleteBeforeSaving() {
        usersDao.insertOrUpdateUser(TestObjects.user)

        usersDao.saveUsers(TestObjects.users)

        val users = usersDao.fetchUsers().getOrAwaitValue()
        assertThat(users, `is`(TestObjects.users))
    }

    @After
    fun tearDown() {
        database.close()
    }
}