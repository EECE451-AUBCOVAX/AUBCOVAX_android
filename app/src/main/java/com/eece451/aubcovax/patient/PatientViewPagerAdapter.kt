package com.eece451.aubcovax.patient;

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PatientViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { PatientDosesFragment() }
            1 -> { PatientMeFragment() }
            else -> PatientDosesFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}