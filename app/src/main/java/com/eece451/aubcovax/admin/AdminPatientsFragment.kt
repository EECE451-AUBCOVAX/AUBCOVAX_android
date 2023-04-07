package com.eece451.aubcovax.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.PatientModel

class AdminPatientsFragment : Fragment() {

    private var listview : ListView? = null
    private var patients : ArrayList<PatientModel>? = ArrayList()
    private var adapter : AdminMedicalPersonnelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 1 .. 9) {
            patients?.add(PatientModel(i, "Patient $i", "$i", "0$i/123456",
                "patient$i@email.com", "0$i-01-2023",
                "Country $i, City $i", "Medical Conditions $i"))
        }

        adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.admin_patients_fragment, container, false)

        listview = view.findViewById(R.id.patientsListView)
        listview?.adapter = AdminPatientsAdapter(layoutInflater, patients!!)

        return view
    }
}