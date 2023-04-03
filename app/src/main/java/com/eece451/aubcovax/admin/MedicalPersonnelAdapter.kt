package com.eece451.aubcovax.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.MedicalPersonnelModel

class MedicalPersonnelAdapter (private val inflater: LayoutInflater,
                               private val dataSource: List<MedicalPersonnelModel>)
    : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.medical_personnel_list_item, parent, false)
        view.findViewById<TextView>(R.id.medicalPersonnelNameTextView).text = dataSource[position].name
        view.findViewById<TextView>(R.id.medicalPersonnelPhoneNumberTextView).text = dataSource[position].phoneNumber
        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return dataSource[position].id?.toLong() ?: 0
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}
