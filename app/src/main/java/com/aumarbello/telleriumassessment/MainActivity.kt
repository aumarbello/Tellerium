package com.aumarbello.telleriumassessment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.aumarbello.telleriumassessment.data.AuthHandler
import com.aumarbello.telleriumassessment.di.DaggerAppComponent
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var authHandler: AuthHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerAppComponent.builder().application(application).create().inject(this)
        val navController = findNavController(R.id.nav_host_fragment)

        if (authHandler.isUserLoggedIn()) {
            navController.run {
                popBackStack()
                navigate(R.id.home)
            }
        }
    }
}