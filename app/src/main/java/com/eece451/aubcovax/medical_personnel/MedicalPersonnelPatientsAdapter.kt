package com.eece451.aubcovax.medical_personnel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.PatientModel

class MedicalPersonnelPatientsAdapter(private val inflater: LayoutInflater,
                                      private var patients: ArrayList<PatientModel>)
    : BaseAdapter() {

    private var originalPatientsList: ArrayList<PatientModel> = patients

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.medical_personnel_patient_list_item,
            parent, false)

        view.findViewById<TextView>(R.id.fullNameTextView).text = patients[position].name
        view.findViewById<TextView>(R.id.phoneNumberTextView).text = patients[position].phoneNumber

        return view
    }

    override fun getItem(position: Int): Any {
        return patients[position]
    }

    override fun getItemId(position: Int): Long {
        return patients[position].id?.toLong() ?: 0
    }

    override fun getCount(): Int {
        return patients.size
    }

    fun setData(data: ArrayList<PatientModel>) {
        patients.clear()
        patients.addAll(data)
    }
}