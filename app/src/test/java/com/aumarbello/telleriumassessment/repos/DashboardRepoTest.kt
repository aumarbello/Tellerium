package com.aumarbello.telleriumassessment.repos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.aumarbello.telleriumassessment.TestObjects
import com.aumarbello.telleriumassessment.db.UserEntity
import com.aumarbello.telleriumassessment.db.UsersDao
import com.aumarbello.telleriumassessment.getOrAwaitValue
import com.aumarbello.telleriumassessment.models.DashboardItem
import com.aumarbello.telleriumassessment.repos.DashboardRepo.Companion.toUsersAge
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class DashboardRepoTest {
    private lateinit var repository: DashboardRepo
    private lateinit var dao: UsersDao

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        dao = Mockito.mock(UsersDao::class.java)

        repository = DashboardRepo(dao)
    }

    @Test
    fun resolveUsersToAppropriateDashboardItems() = runBlocking {
        val response = MutableLiveData<List<UserEntity>>().apply {
            value = TestObjects.users
        }
        `when`(dao.fetchUsers()).thenReturn(response)

        val items = repository.generateDashboardItems().getOrAwaitValue()
        assertThat(items, `is`(TestObjects.dashboardItems))
    }

    @Test
    fun whenUserListIsEmptyReturnEmptyDashboardList() = runBlocking {
        val response = MutableLiveData<List<UserEntity>>().apply {
            value = emptyList()
        }
        `when`(dao.fetchUsers()).thenReturn(response)

        val items = repository.generateDashboardItems().getOrAwaitValue()
        assertThat(items, `is`(emptyList()))
    }

    @Test
    fun whenUsersContainsSingleItemReturnSimpleChart() = runBlocking {
        val response = MutableLiveData<List<UserEntity>>().apply {
            value = TestObjects.users.subList(0, 1)
        }
        `when`(dao.fetchUsers()).thenReturn(response)

        val items = repository.generateDashboardItems().getOrAwaitValue()

        val ageItem = items[2]
        val simpleChart = DashboardItem.PieChartItem(
            "Age distribution",
            1,
            mapOf("${TestObjects.users.first().dateOfBirth.toUsersAge()}" to 1)
        )
        assertEquals(ageItem, simpleChart)
    }

    @Test
    fun whenUserListIsLessThanFourReturnMapWithSingleItem() = runBlocking {
        val logEntries = TestObjects.users.subList(0, 3)
        val response = MutableLiveData<List<UserEntity>>().apply {
            value = logEntries
        }
        `when`(dao.fetchUsers()).thenReturn(response)

        val items = repository.generateDashboardItems().getOrAwaitValue()

        val ageItem = items[2]

        val ageList = logEntries.map { it.dateOfBirth.toUsersAge() }.sorted()
        val simpleChart = DashboardItem.PieChartItem(
            "Age distribution",
            1,
            mapOf("${ageList.first()}-${ageList.last()}" to 1)
        )
        assertEquals(ageItem, simpleChart)
    }

    @Test
    fun whenEmptyAgeGroupsExistRemoveFromDashboardItems() = runBlocking {
        val response = MutableLiveData<List<UserEntity>>().apply {
            value = TestObjects.users.subList(1, TestObjects.users.size)
        }
        `when`(dao.fetchUsers()).thenReturn(response)

        val items = repository.generateDashboardItems().getOrAwaitValue()

        val ageItem = items[2] as DashboardItem.PieChartItem
        ageItem.groups.forEach { (_, value) ->
            assertThat(value, `is`(CoreMatchers.not(0)))
        }
    }
}