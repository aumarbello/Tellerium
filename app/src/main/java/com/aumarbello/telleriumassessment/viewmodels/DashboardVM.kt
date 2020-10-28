package com.aumarbello.telleriumassessment.viewmodels

import com.aumarbello.telleriumassessment.models.DashboardItem
import com.aumarbello.telleriumassessment.repos.DashboardRepo
import javax.inject.Inject

class DashboardVM @Inject constructor(private val repo: DashboardRepo): BaseVM<List<DashboardItem>>() {
    fun populateDashboard() {
        observeData("An error occurred") { repo.generateDashboardItems() }
    }
}