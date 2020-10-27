package com.aumarbello.telleriumassessment.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aumarbello.telleriumassessment.viewmodels.LoginVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindsViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginVM::class)
    abstract fun bindsLoginVM(viewModel: LoginVM): ViewModel
}