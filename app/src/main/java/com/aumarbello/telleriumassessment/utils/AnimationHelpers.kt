package com.aumarbello.telleriumassessment.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

const val midDuration = 400L

fun View.fadeView(fadeIn: Boolean = true) {
    val endAlpha: Float
    val duration: Long
    val listener: AnimatorListenerAdapter?

    if (fadeIn) {
        alpha = 0f
        visibility = View.VISIBLE

        endAlpha = 1f
        duration = midDuration
        listener = null
    } else {
        endAlpha = 0f
        duration = midDuration
        listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
        }
    }

    animate()
        .alpha(endAlpha)
        .setDuration(duration)
        .setListener(listener)
}