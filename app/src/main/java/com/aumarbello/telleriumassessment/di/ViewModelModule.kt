package com.aumarbello.telleriumassessment.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aumarbello.telleriumassessment.viewmodels.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginVM::class)
    abstract fun bindLoginVM(viewModel: LoginVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeVM::class)
    abstract fun bindHomeVM(viewModel: HomeVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardVM::class)
    abstract fun bindDashboardVM(viewModel: DashboardVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UsersVM::class)
    abstract fun bindUsersVM(viewModel: UsersVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserDetailsVM::class)
    abstract fun bindUserDetailsVM(viewModel: UserDetailsVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapVM::class)
    abstract fun bindsMapSharedVM(viewModel: MapVM): ViewModel
}