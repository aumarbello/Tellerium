package com.aumarbello.telleriumassessment.db

import androidx.room.TypeConverter
import com.aumarbello.telleriumassessment.models.UserLocation

class UserLocationConverter {
    private val delimiter = ","

    @TypeConverter
    fun fromUserLocation(location: UserLocation?): String? {
        return if (location != null) {
            "${location.latitude}$delimiter${location.longitude}"
        } else {
            null
        }
    }

    @TypeConverter
    fun toUserLocation(location: String?): UserLocation? {
        if (location == null)
            return null

        val coordinates = location.split(delimiter)
        return UserLocation(coordinates[0].toDouble(), coordinates[1].toDouble())
    }
}

class CoordinatesConverter {
    private val separator = "|"
    private val helper = UserLocationConverter()

    @TypeConverter
    fun fromCoordinates(coordinates: List<UserLocation>): String {
        return coordinates.joinToString(separator) { helper.fromUserLocation(it) ?: "" }
    }

    @TypeConverter
    fun toCoordinates(coordinates: String): List<UserLocation> {
        if (coordinates.isEmpty()) {
            return emptyList()
        }

        val locations = coordinates.split(separator)
        return locations.mapNotNull { helper.toUserLocation(it) }
    }
}