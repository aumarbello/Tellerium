package com.aumarbello.telleriumassessment.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aumarbello.telleriumassessment.*
import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.models.State
import com.aumarbello.telleriumassessment.models.UserLocation
import com.aumarbello.telleriumassessment.utils.Event
import com.aumarbello.telleriumassessment.utils.IsErrorDisplayedMatcher.Companion.isErrorDisplayed
import com.aumarbello.telleriumassessment.utils.TaskIdlingResourceRule
import com.aumarbello.telleriumassessment.utils.TextSetterAction.setText
import com.aumarbello.telleriumassessment.utils.ViewModelUtil
import com.aumarbello.telleriumassessment.viewmodels.MapVM
import com.aumarbello.telleriumassessment.viewmodels.UserDetailsVM
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UserDetailsFragmentTest {
    @get:Rule
    val executorRule = TaskIdlingResourceRule()

    @get:Rule
    val coroutinesRule = CoroutineTestRule()

    @get:Rule
    val intentsTestRule = IntentsTestRule(FragmentScenario.EmptyFragmentActivity::class.java)

    private val state = MutableLiveData<State<UserEntity>>()
    private val locations = MutableLiveData<List<UserLocation>>()
    private val farmLocation = MutableLiveData<UserLocation>()
    private val imagePath = MutableLiveData<String>()
    private lateinit var navController: NavController

    private lateinit var viewModel: UserDetailsVM
    private lateinit var sharedViewModel: MapVM
    private lateinit var scenario: FragmentScenario<UserDetailsFragment>

    @Before
    fun setUp() {
        viewModel = Mockito.mock(UserDetailsVM::class.java)
        sharedViewModel = Mockito.mock(MapVM::class.java)
        
        `when`(viewModel.state).thenReturn(state)

        `when`(sharedViewModel.imagePath).thenReturn(imagePath)
        `when`(sharedViewModel.coordinates).thenReturn(locations)
        `when`(sharedViewModel.farmLocation).thenReturn(farmLocation)

        navController = mock()

        scenario = launchFragmentInContainer(
            themeResId = R.style.TestContainer,
            fragmentArgs = NavGraphDirections.toUserDetails(TestObjects.user.id).arguments
        ) {
            UserDetailsFragment().apply {
                factory = ViewModelUtil.createFor(viewModel, sharedViewModel)
                viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        Navigation.setViewNavController(requireView(), navController)
                    }
                }
            }
        }
    }

    @Test
    fun whenUsersFirstNameNotValidThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")

        onView(withId(R.id.firstName)).perform(typeText("JP"), closeSoftKeyboard())
        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(R.id.firstNameLayout)).check(matches(isErrorDisplayed((R.string.error_first_name))))
    }

    @Test
    fun whenUsersOtherNameNotValidThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")

        onView(withId(R.id.firstName)).perform(typeText("Hobi"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("KP"), closeSoftKeyboard())
        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(R.id.otherNameLayout)).check(matches(isErrorDisplayed((R.string.error_other_names))))
    }

    @Test
    fun whenPhoneNumberIsNotValidThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())

        onView(withId(R.id.phoneNumber)).perform(typeText("08141"), closeSoftKeyboard())
        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(R.id.phoneNumberLayout)).check(matches(isErrorDisplayed(R.string.error_phone_number)))
    }

    @Test
    fun whenAgeProvideIsNotValidThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("08012312312"), closeSoftKeyboard())

        onView(withId(R.id.age)).perform(scrollTo(), setText("2005-01-01"), closeSoftKeyboard())
        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText((R.string.error_age))))
    }

    @Test
    fun whenGenderNotSelectedThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.age)).perform(scrollTo(), setText("2000-01-01"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("08012312312"), closeSoftKeyboard())

        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText((R.string.error_gender))))
    }

    @Test
    fun whenFarmNameNotValidThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.age)).perform(scrollTo(), setText("2000-01-01"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("08012312312"), closeSoftKeyboard())
        onView(withId(R.id.female)).perform(scrollTo(), click())

        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(R.id.farmNameLayout)).check(matches(isErrorDisplayed(R.string.error_farm_name)))
    }

    @Test
    fun whenUserLocationNotSelectedThenDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.age)).perform(scrollTo(), setText("2000-01-01"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("08012312312"), closeSoftKeyboard())
        onView(withId(R.id.female)).perform(scrollTo(), click())
        onView(withId(R.id.farmName)).perform(
            scrollTo(),
            typeText("SK Farms LTD"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText((R.string.error_farm_location))))
    }

    @Test
    fun whenCoordinatesProvidedDontFormPolygonDisplayError() {
        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(typeText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(typeText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.age)).perform(scrollTo(), setText("2000-01-01"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(typeText("08012312312"), closeSoftKeyboard())
        onView(withId(R.id.female)).perform(scrollTo(), click())
        onView(withId(R.id.farmName)).perform(
            scrollTo(),
            typeText("SK Farms LTD"),
            closeSoftKeyboard()
        )

        farmLocation.postValue(UserLocation(6.145, 2.509))

        onView(withId(R.id.save)).perform(scrollTo(), click())

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText((R.string.error_coordinates))))
    }

    @Test
    fun whenInputsAreAllValidThenCallViewModel() {
        state.postValue(State(data = TestObjects.user))

        imagePath.postValue("pictures/imageTaken.png")
        onView(withId(R.id.firstName)).perform(replaceText("Bayo"), closeSoftKeyboard())
        onView(withId(R.id.otherNames)).perform(replaceText("Musa"), closeSoftKeyboard())
        onView(withId(R.id.age)).perform(scrollTo(), setText("2000-01-01"), closeSoftKeyboard())
        onView(withId(R.id.phoneNumber)).perform(replaceText("08012312312"), closeSoftKeyboard())
        onView(withId(R.id.female)).perform(scrollTo(), click())
        onView(withId(R.id.farmName)).perform(
            scrollTo(),
            replaceText("SK Farms LTD"),
            closeSoftKeyboard()
        )
        farmLocation.postValue(UserLocation(6.145, 2.509))

        locations.postValue(
            listOf(
                UserLocation(6.412, 5.981),
                UserLocation(6.302, 5.8710),
                UserLocation(6.292, 5.761),
                UserLocation(6.182, 5.661)
            )
        )

        onView(withId(R.id.save)).perform(scrollTo(), click())

        val user = TestObjects.user.copy(
            firstName = "Bayo",
            lastName = "Musa",
            farmName = "SK Farms LTD",
            dateOfBirth = "2000-01-01",
            imageUrl = "pictures/imageTaken.png",
            phoneNumber = "08012312312",
            gender = "Female",
            location = UserLocation(6.145, 2.509),
            farmCoordinates = listOf(
                UserLocation(6.412, 5.981),
                UserLocation(6.302, 5.8710),
                UserLocation(6.292, 5.761),
                UserLocation(6.182, 5.661)
            )
        )

        verify(viewModel).updateUser(user)
    }

    @Test
    fun whenProfileImageClickedOpenImageFragment() {
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                Intent()
            )
        )

        onView(withId(R.id.profileImage)).perform(click())

        intended(
            allOf(
                hasAction(MediaStore.ACTION_IMAGE_CAPTURE),
                hasExtraWithKey(MediaStore.EXTRA_OUTPUT)
            )
        )
    }

    @Test
    fun whenLoadingThenDisplayProgressBar() {
        state.postValue(State(loading = true))

        onView(withId(R.id.loader)).check(matches(isDisplayed()))
    }

    @Test
    fun whenErrorOccursDisplayErrorView() {
        val event = Event("An error occurred")
        state.postValue(State(error = event))

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(event.peekContent())))
    }

    @Test
    fun whenAddCoordinatesIsClickedThenDisplayLocationOptions() {
        onView(withId(R.id.addCoordinate)).perform(scrollTo(), click())

        onView(withText(R.string.label_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.action_current_location)).check(matches(isDisplayed()))
        onView(withText(R.string.action_pick_on_map)).check(matches(isDisplayed()))
    }

    @Test
    fun whenFarmLayoutIsClickedTheDisplayLocationOptions() {
        onView(withId(R.id.farmLocationLayout)).perform(scrollTo(), click())

        onView(withText(R.string.label_dialog_title)).check(matches(isDisplayed()))
        onView(withText(R.string.action_current_location)).check(matches(isDisplayed()))
        onView(withText(R.string.action_pick_on_map)).check(matches(isDisplayed()))
    }

    @Test
    fun whenCoordinatesLessThanThreeThenHideViewOnMap() {
        //Do nothing, test against initial state

        onView(withId(R.id.viewOnMap)).check(matches(not(isDisplayed())))
    }

    @Test
    fun whenCoordinatesAreThreeOrMoreThenShowViewOnMap() {
        locations.postValue(
            listOf(
                UserLocation(6.412, 5.981),
                UserLocation(6.302, 5.8710),
                UserLocation(6.292, 5.761),
                UserLocation(6.182, 5.661)
            )
        )

        onView(withId(R.id.viewOnMap)).perform(scrollTo())
        onView(withId(R.id.viewOnMap)).check(matches(isDisplayed()))
    }
}