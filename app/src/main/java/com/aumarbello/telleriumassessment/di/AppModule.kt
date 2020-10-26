package com.aumarbello.telleriumassessment.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    @Singleton
    fun providesApplicationContext(application: Application): Context {
        return application
    }
}