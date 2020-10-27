package com.aumarbello.telleriumassessment.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.aumarbello.telleriumassessment.CoroutineTestRule
import com.aumarbello.telleriumassessment.mock
import com.aumarbello.telleriumassessment.models.State
import com.aumarbello.telleriumassessment.repos.LoginRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class LoginVMTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var repo: LoginRepo
    private lateinit var viewModel: LoginVM

    @Captor
    private lateinit var captor: ArgumentCaptor<State<Boolean>>

    @Before
    fun setUp() {
        repo = mock(LoginRepo::class.java)
        MockitoAnnotations.initMocks(this)

        viewModel = LoginVM(repo)
    }

    @Test
    fun transitionThroughStates() = runBlockingTest {
        val email = "test@example.com"
        val password = "password"

        val observer = mock<Observer<State<Boolean>>>()
        viewModel.state.observeForever(observer)

        viewModel.login(email, password)

        verify(observer, times(3)).onChanged(captor.capture())
        val initialResponse = captor.allValues.first()
        val finalResponse = captor.allValues.last()

        assertTrue(initialResponse.loading)
        assertFalse(finalResponse.loading)
    }

    @Test
    fun whenLoginSuccessfulUpdateResponse() = runBlockingTest {
        val email = "test@example.com"
        val password = "password"

        `when`(repo.login(anyString(), anyString())).thenReturn(true)


        viewModel.login(email, password)

        val observer = mock<Observer<State<Boolean>>>()
        viewModel.state.observeForever(observer)

        verify(observer).onChanged(State(loading = false, data = true))
    }

    @Test
    fun whenLoginFailsOccursUpdateError() = runBlockingTest {
        val errorMsg =  "Failed to login, try again later."
        val email = "test@example.com"
        val password = "password"

        `when`(repo.login(anyString(), anyString())).thenThrow(IllegalArgumentException(errorMsg))

        viewModel.login(email, password)

        val observer = mock<Observer<State<Boolean>>>()
        viewModel.state.observeForever(observer)

        assertThat(viewModel.state.value?.error?.peekContent(), `is`(errorMsg))
    }
}