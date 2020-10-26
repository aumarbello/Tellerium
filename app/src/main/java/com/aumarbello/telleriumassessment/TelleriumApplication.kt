package com.aumarbello.telleriumassessment

import androidx.multidex.MultiDexApplication
import com.mapbox.mapboxsdk.Mapbox
import timber.log.Timber

class TelleriumApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
    }
}