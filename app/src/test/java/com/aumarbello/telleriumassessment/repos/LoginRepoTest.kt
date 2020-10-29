package com.aumarbello.telleriumassessment.repos

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class LoginRepoTest {
    private lateinit var repo: LoginRepo

    @Before
    fun setUp() {
        repo = LoginRepo(FakePreferences())
    }

    @Test
    fun whenCredentialsAreValidReturnTrue() = runBlockingTest {
        val email = "test@tellerium.com"
        val password = "password"

        val response = repo.login(email, password)

        assertThat(response, `is`(true))
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenCredentialsAreInvalidThrowException() = runBlockingTest {
        val email = "test@tell.com"
        val password = "pass"

        repo.login(email, password)
    }
}