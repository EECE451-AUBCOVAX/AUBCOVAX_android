package com.eece451.aubcovax.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.MedicalPersonnelModel

class AdminMedicalPersonnelFragment : Fragment() {

    private var listview : ListView ? = null
    private var medicalPersonnel : ArrayList<MedicalPersonnelModel> ? = ArrayList()
    private var adapter : AdminMedicalPersonnelAdapter ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 1 .. 9) {
            medicalPersonnel?.add(MedicalPersonnelModel(i, "Person $i", "0$i/123456"))
        }

        adapter?.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.admin_medical_personnel_fragment, container, false)

        listview = view.findViewById(R.id.medicalPersonnelListView)
        listview?.adapter = AdminMedicalPersonnelAdapter(layoutInflater, medicalPersonnel!!)

        return view
    }
}