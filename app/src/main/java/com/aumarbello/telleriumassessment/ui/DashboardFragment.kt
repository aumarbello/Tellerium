package com.aumarbello.telleriumassessment.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.databinding.FragmentDashboardBinding
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import com.aumarbello.telleriumassessment.utils.GridSpacingDecorator
import com.aumarbello.telleriumassessment.utils.appComponent
import com.aumarbello.telleriumassessment.utils.fadeView
import com.aumarbello.telleriumassessment.utils.showSnackBar
import com.aumarbello.telleriumassessment.viewmodels.DashboardVM
import javax.inject.Inject

class DashboardFragment: Fragment(R.layout.fragment_dashboard) {
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var viewModel: DashboardVM
    private lateinit var binding: FragmentDashboardBinding

    private val adapter = DashboardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent?.inject(this)
        viewModel = ViewModelProvider(this, factory)[DashboardVM::class.java]
        viewModel.populateDashboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDashboardBinding.bind(view)

        binding.dashboardItems.also {
            it.adapter = adapter
            it.addItemDecoration(GridSpacingDecorator())
        }

        setObservers()
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            binding.loader.fadeView(it.loading)

            it.error?.getContentIfNotHandled()?.let { msg -> showSnackBar(msg) }

            if (it.data != null) {
                adapter.submitList(it.data)
            }
        })
    }
}