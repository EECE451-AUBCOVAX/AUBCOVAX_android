package com.eece451.aubcovax.patient;

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eece451.aubcovax.patient.PatientDoses
import com.eece451.aubcovax.patient.PatientMe

class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> { PatientDoses() }
            1 -> { PatientMe() }
            else -> PatientDoses()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}