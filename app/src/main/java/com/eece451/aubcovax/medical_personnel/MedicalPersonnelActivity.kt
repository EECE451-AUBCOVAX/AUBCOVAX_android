package com.eece451.aubcovax.medical_personnel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.eece451.aubcovax.R
import com.eece451.aubcovax.patient.PatientViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MedicalPersonnelActivity : AppCompatActivity() {

    private var viewpager2 : ViewPager2? = null
    private var viewPagerAdapter : MedicalPersonnelViewPagerAdapter? = null
    private var bottomNavigationView : BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_personnel_activity)

        viewpager2 = findViewById(R.id.viewPager2)
        viewPagerAdapter = MedicalPersonnelViewPagerAdapter(supportFragmentManager, lifecycle)
        viewpager2?.adapter = viewPagerAdapter

        bottomNavigationView = findViewById(R.id.patientBottomNavigationView)
        bottomNavigationView?.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.patients -> {
                    viewpager2?.currentItem = 0
                    true
                }
                R.id.me -> {
                    viewpager2?.currentItem = 1
                    true
                }
                else -> false
            }
        }

        viewpager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottomNavigationView?.selectedItemId = R.id.patients
                    1 -> bottomNavigationView?.selectedItemId = R.id.me
                }
            }
        })
    }
}