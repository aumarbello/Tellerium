package com.aumarbello.telleriumassessment.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.aumarbello.telleriumassessment.getOrAwaitValue
import com.aumarbello.telleriumassessment.models.UserLocation
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MapVMTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    private lateinit var viewModel: MapVM

    @Before
    fun setUp() {
        viewModel = MapVM()
    }

    @Test
    fun whenImageIsCapturedThenUpdateImagePath() {
        val path = "path/to/image"

        viewModel.setImagePath(path)

        assertThat(viewModel.imagePath.getOrAwaitValue(), `is`(path))
    }

    @Test
    fun whenCoordinateIsAddedThenUpdateUserLocations() {
        val location = UserLocation(6.412, 5.981)

        viewModel.addCoordinate(location)

        assertThat(viewModel.coordinates.getOrAwaitValue(), `is`(listOf(location)))
    }

    @Test
    fun whenCoordinateIsRemovedThenUpdateUserLocations() {
        val locations = listOf (
            UserLocation(6.412, 5.981),
            UserLocation(6.302, 5.8710),
            UserLocation(6.292, 5.761)
        )
        locations.forEach {
            viewModel.addCoordinate(it)
        }

        viewModel.removeCoordinate(UserLocation(6.302, 5.8710))

        val updatedList = listOf (
            UserLocation(6.412, 5.981),
            UserLocation(6.292, 5.761)
        )
        assertThat(viewModel.coordinates.getOrAwaitValue(), `is`(updatedList))
    }

    @Test
    fun whenLocationIsSelectedThenUpdateUserLocation() {
        val location = UserLocation(6.302, 5.8710)

        viewModel.setFarmLocation(location)

        assertThat(viewModel.farmLocation.getOrAwaitValue(), `is`(location))
    }
}