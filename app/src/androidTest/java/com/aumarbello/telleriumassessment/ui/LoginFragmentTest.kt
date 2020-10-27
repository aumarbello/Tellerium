package com.aumarbello.telleriumassessment.ui

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aumarbello.telleriumassessment.CoroutineTestRule
import com.aumarbello.telleriumassessment.mock
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.models.State
import com.aumarbello.telleriumassessment.utils.Event
import com.aumarbello.telleriumassessment.utils.IsErrorDisplayedMatcher.Companion.isErrorDisplayed
import com.aumarbello.telleriumassessment.utils.TaskIdlingResourceRule
import com.aumarbello.telleriumassessment.utils.ViewModelUtil
import com.aumarbello.telleriumassessment.viewmodels.LoginVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LoginFragmentTest {
    @get:Rule
    val executorRule = TaskIdlingResourceRule()

    @get:Rule
    val coroutinesRule = CoroutineTestRule()

    private val state = MutableLiveData<State<Boolean>>()
    private val navController = mock<NavController>()

    private lateinit var viewModel: LoginVM

    @Before
    fun setUp() {
        viewModel = Mockito.mock(LoginVM::class.java)

        `when`(viewModel.state).thenReturn(state)

        val scenario = launchFragmentInContainer(null, R.style.TestContainer) {
            LoginFragment().apply {
                factory = ViewModelUtil.createFor(viewModel)
            }
        }

        scenario.onFragment {
            Navigation.setViewNavController(it.requireView(), navController)
        }
    }

    @Test
    fun whenCredentialsAreValidPassValuesToViewModel() {
        val email = "john@example.com"
        val password = "password"

        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())

        verify(viewModel).login(email, password)
    }

    @Test
    fun whenEmailAddressIsInvalidShowError() {
        val email = "john"
        val password = "password"

        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        onView(withId(R.id.emailLayout)).check(matches(isErrorDisplayed()))
    }

    @Test
    fun whenPasswordIsInvalidShowError() {
        val email = "john@example.com"
        val password = "pas"

        onView(withId(R.id.email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText(password), closeSoftKeyboard())

        onView(withId(R.id.login)).perform(click())

        onView(withId(R.id.passwordLayout)).check(matches(isErrorDisplayed()))
    }

    @Test
    fun whenLoaderIsTrueDisplayProgressBar() {
        state.postValue(State(true))

        onView(withId(R.id.loader)).check(matches(isDisplayed()))
    }

    @Test
    fun whenErrorOccursDisplayErrorView() {
        val event = Event("An error occurred")
        state.postValue(State(error = event))

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(event.peekContent())))
    }
}