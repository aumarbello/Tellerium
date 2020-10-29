package com.aumarbello.telleriumassessment.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aumarbello.telleriumassessment.OpenForTesting
import com.aumarbello.telleriumassessment.models.UserLocation
import javax.inject.Inject

@OpenForTesting
class MapVM @Inject constructor(): ViewModel() {
    private val _locations = MutableLiveData<List<UserLocation>>()
    val coordinates: LiveData<List<UserLocation>> = _locations

    private val _imagePath = MutableLiveData<String>()
    val imagePath: LiveData<String> = _imagePath

    private val _farmLocation = MutableLiveData<UserLocation>()
    val farmLocation: LiveData<UserLocation> = _farmLocation

    fun setImagePath(path: String) {
        _imagePath.value = path
    }

    fun addCoordinate(location: UserLocation) {
        val currentLocations = (_locations.value ?: listOf()).toMutableList()
        currentLocations.add(location)

        _locations.value = currentLocations.toList()
    }

    fun removeCoordinate(location: UserLocation) {
        val currentLocations = _locations.value ?: return
        _locations.value = currentLocations.toMutableList().apply {
            remove(location)
        }.toList()
    }

    fun setFarmLocation(location: UserLocation) {
        _farmLocation.value = location
    }
}