package com.aumarbello.telleriumassessment.viewmodels

import com.aumarbello.telleriumassessment.repos.HomeRepo
import javax.inject.Inject

class HomeVM @Inject constructor(private val repo: HomeRepo): BaseVM<Int>() {
    fun fetchUsers(limit: Int, isInitialLoad: Boolean = false) {
        loadData("Failed to fetch users") { repo.loadUsers(limit, isInitialLoad) }
    }
}