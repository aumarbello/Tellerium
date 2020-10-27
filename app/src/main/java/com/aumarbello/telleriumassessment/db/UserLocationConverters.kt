package com.aumarbello.telleriumassessment.db

import androidx.room.TypeConverter
import com.aumarbello.telleriumassessment.models.UserLocation

class UserLocationConverter {
    private val delimiter = ","

    @TypeConverter
    fun fromUserLocation(location: UserLocation): String {
        return "${location.latitude}$delimiter${location.longitude}"
    }

    @TypeConverter
    fun toUserLocation(location: String): UserLocation {
        val coordinates = location.split(delimiter)
        return UserLocation(coordinates[0].toDouble(), coordinates[1].toDouble())
    }
}

class CoordinatesConverter {
    private val separator = "|"
    private val helper = UserLocationConverter()

    @TypeConverter
    fun fromCoordinates(coordinates: List<UserLocation>): String {
        return coordinates.joinToString(separator) { helper.fromUserLocation(it) }
    }

    @TypeConverter
    fun toCoordinates(coordinates: String): List<UserLocation> {
        val locations = coordinates.split(separator)

        return locations.map { helper.toUserLocation(it) }
    }
}