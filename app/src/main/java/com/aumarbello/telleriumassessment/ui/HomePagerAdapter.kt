package com.aumarbello.telleriumassessment.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class HomePagerAdapter (manager: FragmentManager) : FragmentPagerAdapter(
    manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        return when(position) {
            1 -> UsersFragment()

            else -> DashboardFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 1) {
            "Users"
        } else {
            "Dashboard"
        }
    }

    override fun getCount() = 2
}