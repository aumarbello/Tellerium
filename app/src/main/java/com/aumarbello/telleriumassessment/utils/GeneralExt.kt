package com.aumarbello.telleriumassessment.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.TelleriumApplication
import com.aumarbello.telleriumassessment.di.AppComponent
import com.aumarbello.telleriumassessment.di.DaggerAppComponent
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.File

val Fragment.appComponent: AppComponent?
get() {
    val application = activity?.application
    return if (application is TelleriumApplication)
        DaggerAppComponent.builder().application(application).create()
    else
        null
}

fun Fragment.showSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showSnackBar(message: Int) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.closeKeyboard() {
    view?.closeKeyboard()
}

fun View.closeKeyboard() {
    val imm = context.getSystemService<InputMethodManager>()
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.loadImage(url: String) {
    if (url.startsWith("https")) {
        Picasso.get().load(url).into(this)
    } else {
        val imageFile = File(url)

        if (imageFile.exists()) {
            Picasso.get().load(imageFile)
                .resizeDimen(R.dimen.image_size, R.dimen.image_size)
                .into(this)
        }
    }
}

fun Fragment.updateToolbarTitle(@StringRes title: Int, showBackButton: Boolean = false) {
    val activity = requireActivity()
    if (activity is AppCompatActivity) {
        activity.supportActionBar?.also {
            it.title = getString(title)
            it.setDisplayHomeAsUpEnabled(showBackButton)
        }
    }
}