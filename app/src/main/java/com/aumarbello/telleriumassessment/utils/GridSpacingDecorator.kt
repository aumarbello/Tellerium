package com.aumarbello.telleriumassessment.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.aumarbello.telleriumassessment.R

class GridSpacingDecorator (private val spacing: Int = DEFAULT_SPACING): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemSpacing = if (spacing == DEFAULT_SPACING)
            view.resources.getDimension(R.dimen.spacing_medium).toInt()
        else
            spacing

        val position = parent.getChildAdapterPosition(view)
        val isFirstColumn = position % 2 == 0
        if (isFirstColumn) {
            outRect.left = itemSpacing
            outRect.right = itemSpacing.div(2)
        } else {
            outRect.left = itemSpacing.div(2)
            outRect.right = itemSpacing
        }

        val itemCount = parent.adapter?.itemCount ?: return
        val isLastRow = if (itemCount % 2 == 0) {
            //Any of the last two items
            position == itemCount.minus(2) || position == itemCount.dec()
        } else {
            //Just last item
            position == itemCount.dec()
        }

        if (isLastRow) {
            outRect.bottom = itemSpacing
            outRect.top = itemSpacing
        } else {
            outRect.top = itemSpacing
        }
    }

    private companion object {
        const val DEFAULT_SPACING = -1
    }
}