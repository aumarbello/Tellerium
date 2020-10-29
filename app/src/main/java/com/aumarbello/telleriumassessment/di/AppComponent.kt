package com.aumarbello.telleriumassessment.di

import android.app.Application
import com.aumarbello.telleriumassessment.MainActivity
import com.aumarbello.telleriumassessment.ui.*
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

    fun inject(activity: MainActivity)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: DashboardFragment)
    fun inject(fragment: UsersFragment)
    fun inject(fragment: UserDetailsFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: MapFragment)
}