package com.aumarbello.telleriumassessment.di

import android.app.Application
import android.content.Context
import com.aumarbello.telleriumassessment.data.AppPreferences
import com.aumarbello.telleriumassessment.data.Preferences
import com.aumarbello.telleriumassessment.db.TelleriumDatabase
import com.aumarbello.telleriumassessment.db.UsersDao
import com.aumarbello.telleriumassessment.remote.UsersService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Provides
    @Singleton
    fun providesApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesAuthHandler(context: Context): Preferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun providesUsersDao(context: Context): UsersDao {
        return TelleriumDatabase.getInstance(context).usersDao()
    }

    @Provides
    @Singleton
    fun providesUsersService(): UsersService {
        val client = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://theagromall.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(UsersService::class.java)
    }
}