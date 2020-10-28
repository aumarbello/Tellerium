package com.aumarbello.telleriumassessment.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.aumarbello.telleriumassessment.ui.DashboardHolder.*
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.models.DashboardItem

class DashboardAdapter: ListAdapter<DashboardItem, DashboardHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            bar -> {
               val view = inflater.inflate(R.layout.item_dashboard_bar_chart, parent, false)
                BarHolder(view)
            }
            pie -> {
                val view = inflater.inflate(R.layout.item_dashboard_pie_chart, parent, false)
                PieHolder(view)
            }

            else -> {
                val view = inflater.inflate(R.layout.item_dashboard_count, parent, false)
                CountHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: DashboardHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is DashboardItem.CountItem -> count
            is DashboardItem.BarChartItem -> bar
            is DashboardItem.PieChartItem -> pie
        }
    }

    private companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DashboardItem>() {
            override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DashboardItem,
                newItem: DashboardItem
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val count = 10
        private const val bar = 20
        private const val pie = 30
    }
}