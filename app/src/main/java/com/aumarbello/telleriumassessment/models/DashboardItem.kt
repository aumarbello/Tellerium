package com.aumarbello.telleriumassessment.models

sealed class DashboardItem {
    data class CountItem (
        val title: String,
        val count: Int
    ): DashboardItem()

    data class BarChartItem (
        val title: String,
        val items: Map<String, Int>
    ): DashboardItem()

    data class PieChartItem (
        val title: String,
        val total: Int,
        val groups: Map<String, Int>
    ): DashboardItem()
}