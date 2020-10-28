package com.aumarbello.telleriumassessment.ui

import android.graphics.Color
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.databinding.ItemDashboardBarChartBinding
import com.aumarbello.telleriumassessment.databinding.ItemDashboardCountBinding
import com.aumarbello.telleriumassessment.databinding.ItemDashboardPieChartBinding
import com.aumarbello.telleriumassessment.models.DashboardItem
import com.aumarbello.telleriumassessment.models.DashboardItem.PieChartItem
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class DashboardHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bindItem(item: DashboardItem)

    class CountHolder(view: View) : DashboardHolder(view) {
        private val binding = ItemDashboardCountBinding.bind(view)

        override fun bindItem(item: DashboardItem) {
            with(item as DashboardItem.CountItem) {
                binding.title.text = title
                binding.count.text = count.toString()
            }
        }
    }

    class BarHolder(view: View) : DashboardHolder(view) {
        private val binding = ItemDashboardBarChartBinding.bind(view)

        override fun bindItem(item: DashboardItem) {
            with(item as DashboardItem.BarChartItem) {
                binding.title.text = title
                binding.barChart.apply {
                    description = null
                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false
                    isDoubleTapToZoomEnabled = false
                    legend.isEnabled = false
                    xAxis.apply {
                        valueFormatter = object : ValueFormatter() {
                            val labels = item.items.keys.toList()
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                return labels[value.toInt()][0].toString()
                            }

                        }
                        position = XAxis.XAxisPosition.BOTTOM
                        gridColor = Color.TRANSPARENT
                        textSize = 9f
                        textColor = ResourcesCompat.getColor(itemView.resources, R.color.colorOnSurface, itemView.context.theme)
                    }

                    data = toBarData()
                    invalidate()
                }
            }
        }

        private fun DashboardItem.BarChartItem.toBarData(): BarData {
            var pos = 0
            val entries = items.map {
                BarEntry(
                    pos++.toFloat(),
                    it.value.toFloat(),
                    it.key
                )
            }
            val dataSet = BarDataSet(entries, "").apply {
                colors = getColorsForGraph(1)
                valueFormatter = object : ValueFormatter() {
                    override fun getBarLabel(barEntry: BarEntry?): String {
                        return barEntry?.y?.toInt()?.toString() ?: "0"
                    }
                }
                valueTextColor = ResourcesCompat.getColor(itemView.resources, R.color.colorOnSurface, itemView.context.theme)
            }
            return BarData(dataSet).apply {
                barWidth = 0.9f
            }
        }
    }

    class PieHolder(view: View) : DashboardHolder(view) {
        private val binding = ItemDashboardPieChartBinding.bind(view)

        override fun bindItem(item: DashboardItem) {
            with(item as PieChartItem) {
                binding.title.text = title
                binding.pieChart.apply {
                    description = null
                    isRotationEnabled = false
                    data = toPieData()

                    setEntryLabelTextSize(7f)
                    setUsePercentValues(true)
                    setEntryLabelColor(Color.DKGRAY)

                    setHoleColor(ResourcesCompat.getColor(itemView.resources, R.color.colorSurface, itemView.context.theme))

                    legend.also {
                        it.textColor = ResourcesCompat.getColor(itemView.resources, R.color.colorOnSurface, itemView.context.theme)
                        it.textSize = 9f
                    }

                    invalidate()
                }
            }
        }

        private fun PieChartItem.toPieData(): PieData {
            val entries = groups.map { PieEntry(it.value.toFloat(), it.key) }
            val dataSet = PieDataSet(entries, "").apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                        return "${value.toInt()}%"
                    }
                }
                valueTextSize = 8f
                colors = getColorsForGraph(entries.size)
            }

            return PieData(dataSet)
        }
    }

    private companion object {
        fun getColorsForGraph(numberOfColors: Int): List<Int> {
            val colors = listOf(Color.parseColor("#E4BE9E"), Color.parseColor("#FAFF70"), Color.parseColor("#6564DB"))

            return colors.subList(0, numberOfColors)
        }
    }
}