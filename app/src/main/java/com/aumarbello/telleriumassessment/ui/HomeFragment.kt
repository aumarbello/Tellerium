package com.aumarbello.telleriumassessment.ui

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aumarbello.telleriumassessment.R
import com.aumarbello.telleriumassessment.databinding.FragmentHomeBinding
import com.aumarbello.telleriumassessment.di.ViewModelFactory
import com.aumarbello.telleriumassessment.utils.*
import com.aumarbello.telleriumassessment.viewmodels.HomeVM
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home) {
    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var viewModel: HomeVM
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent?.inject(this)
        viewModel = ViewModelProvider(this, factory)[HomeVM::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)

        updateToolbarTitle(R.string.label_home)
        updateUsers(10)
        binding.pager.adapter = HomePagerAdapter(childFragmentManager)

        setObserver()
    }

    private fun showDialog() {
        val view = View.inflate(requireContext(), R.layout.dialog_users_input, null)
        val input = view.findViewById<EditText>(R.id.limit)
        val confirm = view.findViewById<View>(R.id.confirm)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        confirm.setOnClickListener {
            if (input.text.toString().isEmpty()) {
                showToast("Invalid input entered")
                return@setOnClickListener
            }

            val count = input.text.toString().toInt()
            val total = viewModel.state.value?.data ?: 0
            if (count > total) {
                showToast("Count can't be greater the total")
                return@setOnClickListener
            }

            updateUsers(count)
            dialog.cancel()
        }

        dialog.show()
    }

    private fun setObserver() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
            binding.loader.fadeView(it.loading)

            it.error?.getContentIfNotHandled()?.let { msg -> showSnackBar(msg) }

            if (it.data != null) {
                binding.total.text = getString(R.string.format_total, it.data)
                binding.limit.setOnClickListener { showDialog() }
            }
        })
    }

    private fun updateUsers(limit: Int) {
        binding.limit.text = limit.toString()
        viewModel.fetchUsers(limit)
    }
}