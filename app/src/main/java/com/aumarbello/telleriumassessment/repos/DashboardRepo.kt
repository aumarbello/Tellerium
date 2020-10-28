package com.aumarbello.telleriumassessment.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.aumarbello.telleriumassessment.OpenForTesting
import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.db.UsersDao
import com.aumarbello.telleriumassessment.models.DashboardItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.math.min

@OpenForTesting
class DashboardRepo @Inject constructor(private val dao: UsersDao) {
    suspend fun generateDashboardItems(): LiveData<List<DashboardItem>> = withContext(Dispatchers.IO) {
        dao.fetchUsers().map { getDashBoardItems(it) }
    }

    private fun getDashBoardItems(entities: List<UserEntity>): List<DashboardItem> {
        if (entities.isEmpty())
            return emptyList()

        val items = mutableListOf<DashboardItem>()
        items.add(DashboardItem.CountItem("Total Users", entities.size))
        items.add(DashboardItem.CountItem(
            "Users with complete profile",
            entities.count { it.location != null && it.farmCoordinates.isNotEmpty() }
        ))
        items.add(resolveAgeDistribution(entities))
        items.add(resolveGenderDistribution(entities))
        items.add(resolveStatusDistribution(entities))

        return items.toList()
    }

    private fun resolveAgeDistribution(entities: List<UserEntity>): DashboardItem.PieChartItem {
        val title = "Age distribution"
        return when {
            entities.size == 1 -> DashboardItem.PieChartItem(
                title,
                1,
                mapOf("${entities.first().dateOfBirth.toUsersAge()}" to 1)
            )

            entities.size < 4 -> {
                val ageList = entities.map { it.dateOfBirth.toUsersAge() }.sorted()
                DashboardItem.PieChartItem(
                    title,
                    1,
                    mapOf("${ageList.first()}-${ageList.last()}" to 1)
                )
            }

            else -> {
                val ageMap = mutableMapOf<String, Int>()
                val ageList = entities.map { it.dateOfBirth.toUsersAge() }.sorted()

                val youngest = ageList.first()
                val oldest = ageList.last()
                val ageDifference = oldest - youngest

                var groups = if (ageDifference < 9)
                    2
                else
                    3
                val interval = ageDifference / groups

                var lowerBound = youngest
                var upperBound = min(lowerBound + interval, oldest)

                while (groups > 0) {
                    ageMap["$lowerBound-$upperBound"] = 0

                    lowerBound = upperBound.inc()
                    upperBound = min(lowerBound + interval, oldest)

                    groups--
                }

                val ageRanges = ageMap.keys.toList().map {
                    val range = it.split("-")
                    IntRange(range.first().toInt(), range.last().toInt())
                }

                entities.forEach {
                    val range = ageRanges.find { age -> age.contains(it.dateOfBirth.toUsersAge()) } ?: return@forEach
                    val key = "${range.first}-${range.last}"
                    val currentValue = ageMap[key] ?: return@forEach

                    ageMap[key] = currentValue.inc()
                }

                DashboardItem.PieChartItem(title, entities.size, ageMap.filter { it.value != 0 })
            }
        }
    }

    private fun resolveGenderDistribution(entities: List<UserEntity>): DashboardItem.PieChartItem {
        fun foldGender(gender: String): Pair<String, Int> {
            val value = entities.fold(0) { currentValue, entity ->
                if (entity.gender == gender)
                    currentValue.inc()
                else
                    currentValue
            }

            return gender to value
        }

        return DashboardItem.PieChartItem(
            "Gender distribution",
            entities.size,
            mapOf(foldGender("Male"), foldGender("Female")).filter { it.value != 0 }
        )
    }

    private fun resolveStatusDistribution(entities: List<UserEntity>): DashboardItem.PieChartItem {
        fun foldStatus(status: String): Pair<String, Int> {
            val value = entities.fold(0) { currentValue, entity ->
                if (entity.maritalStatus == status)
                    currentValue.inc()
                else
                    currentValue
            }

            return status to value
        }

        return DashboardItem.PieChartItem(
            "Marital status distribution",
            entities.size,
            mapOf(foldStatus("Married"), foldStatus("Single")).filter { it.value != 0 }
        )
    }

    companion object {
        fun String.toUsersAge(): Int {
            return if (isEmpty()) {
                return 0
            } else {
                val currentYear = Calendar.getInstance()[Calendar.YEAR]
                val usersYear = split("-").firstOrNull()?.toInt() ?: currentYear
                currentYear - usersYear
            }
        }
    }
}