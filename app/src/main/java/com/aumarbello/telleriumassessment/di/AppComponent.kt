package com.aumarbello.telleriumassessment.di

import android.app.Application
import com.aumarbello.telleriumassessment.ui.LoginFragment
import com.aumarbello.telleriumassessment.MainActivity
import com.aumarbello.telleriumassessment.ui.HomeFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun create(): AppComponent
    }

    fun inject(fragment: LoginFragment)
    fun inject(activity: MainActivity)
    fun inject(fragment: HomeFragment)
}