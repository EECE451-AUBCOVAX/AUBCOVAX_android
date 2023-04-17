package com.eece451.aubcovax.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.eece451.aubcovax.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class PatientActivity : AppCompatActivity() {

    private var viewpager2 : ViewPager2? = null
    private var viewPagerAdapter : PatientViewPagerAdapter? = null
    private var bottomNavigationView : BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_activity)

        viewpager2 = findViewById(R.id.viewPager2)
        viewPagerAdapter = PatientViewPagerAdapter(supportFragmentManager, lifecycle)
        viewpager2?.adapter = viewPagerAdapter

        bottomNavigationView = findViewById(R.id.patientBottomNavigationView)
        bottomNavigationView?.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.doses -> {
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
                    0 -> bottomNavigationView?.selectedItemId = R.id.doses
                    1 -> bottomNavigationView?.selectedItemId = R.id.me
                }
            }
        })
    }
}