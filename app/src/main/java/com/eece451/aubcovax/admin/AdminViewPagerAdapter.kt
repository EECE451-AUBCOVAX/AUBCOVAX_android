package com.eece451.aubcovax.admin;

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { AdminPatientsFragment() }
            1 -> { AdminMedicalPersonnelFragment() }
            2 -> { AdminMeFragment() }
            else -> AdminPatientsFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}