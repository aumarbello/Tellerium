package com.aumarbello.telleriumassessment.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aumarbello.telleriumassessment.NavGraphDirections
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import com.aumarbello.telleriumassessment.utils.appComponent
import com.aumarbello.telleriumassessment.utils.midDuration
import com.aumarbello.telleriumassessment.viewmodels.UsersVM
import com.google.android.material.transition.MaterialElevationScale
import javax.inject.Inject

class UsersFragment : Fragment(R.layout.fragment_users), UsersAdapter.CallBack {
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var viewModel: UsersVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent?.inject(this)
        viewModel = ViewModelProvider(this, factory)[UsersVM::class.java]
        viewModel.loadUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val usersList: RecyclerView = view.findViewById(R.id.users)
        val adapter = UsersAdapter(this)
        usersList.adapter = adapter
        postponeEnterTransition()

        viewModel.state.observe(viewLifecycleOwner, Observer {
            if (it.data != null) {
                adapter.submitList(it.data)
            }
        })
    }

    override fun openDetails(view: View, userId: String) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = midDuration
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = midDuration
        }

        val extra = FragmentNavigatorExtras(view to getString(R.string.transition_name_user_details))
        val directions = NavGraphDirections.toUserDetails(userId)
        findNavController().navigate(directions, extra)
    }
}