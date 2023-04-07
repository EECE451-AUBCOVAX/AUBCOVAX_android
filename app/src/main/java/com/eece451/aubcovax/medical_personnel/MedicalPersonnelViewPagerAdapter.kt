package com.eece451.aubcovax.medical_personnel;

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MedicalPersonnelViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { MedicalPersonnelPatientsFragment() }
            1 -> { MedicalPersonnelMeFragment() }
            else -> MedicalPersonnelMeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}