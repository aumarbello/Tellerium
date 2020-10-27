package com.aumarbello.telleriumassessment

import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.models.DashboardItem
import com.aumarbello.telleriumassessment.models.UserLocation

object TestObjects {
    private val location = UserLocation(25.774, -80.190)
    private val coordinates = listOf (
        UserLocation(25.774, -80.190),
        UserLocation(18.466, -66.118),
        UserLocation(32.321, -64.757)
    )

    private const val imagePath = "home/aumarbello/Pictures/example.png"

    val users = listOf(
        UserEntity("1", "Ola", "Musa", "Musa and sons Farm", "1985-01-01", imagePath,"08132434141", "joe@example.com", "M", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Single", "REF/429"),
        UserEntity("2", "Chukwudi", "Deji", "CK Farms", "1991-01-01", imagePath,"08132434141", "joe@example.com", "F", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Married", "REF/429"),
        UserEntity("3", "Sanni", "Banjo", "SB Farm Ltd", "1982-01-01", imagePath,"08132434141", "joe@example.com", "M", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Single", "REF/429"),
        UserEntity("4", "Pauline", "John", "PJ Industrial Farm", "1989-01-01", imagePath,"08132434141", "joe@example.com", "F", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Single", "REF/429"),
        UserEntity("5", "Seyi", "Kano", "SK Allied Farm", "1983-01-01", imagePath,"08132434141", "joe@example.com", "F", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Married", "REF/429")
    )


    val user = UserEntity("15", "Lizzy", "James", "L&J Farms", "1983-01-01", imagePath,"08132434141", "joe@example.com", "F", location, coordinates, "Gilfoy Avenue", "221B Baker Street", "LD", "Married", "REF/429")

    val dashboardItems = listOf(
        DashboardItem.CountItem("Users onboarded", 5),
        DashboardItem.BarChartItem(
            "Week's progress",
            mapOf(
                "Sun" to 0,
                "Mon" to 2,
                "Tue" to 1,
                "Wed" to 1,
                "Thur" to 1,
                "Fri" to 0,
                "Sat" to 0
            )
        ),
        DashboardItem.PieChartItem(
            "Age distribution",
            5,
            mapOf("29-32" to 2, "33-36" to 1, "37-38" to 2)
        ),
        DashboardItem.PieChartItem("Gender distribution", 5, mapOf("M" to 3, "F" to 2))
    )
}