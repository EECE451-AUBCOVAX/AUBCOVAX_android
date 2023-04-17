package com.eece451.aubcovax.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.PatientModel

class AdminPatientsAdapter (private val inflater: LayoutInflater,
                            private val patients: List<PatientModel>)
    : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.admin_patient_list_item, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val patient = patients[position]

        viewHolder.fullNameTextView.text = "${patient.firstName} ${patient.lastName}"
        viewHolder.cardNumberTextView.text = patient.idCardNumber
        viewHolder.phoneNumberTextView.text = patient.phoneNumber
        viewHolder.emailTextView.text = patient.email
        viewHolder.dateOfBirthTextView.text = patient.dateOfBirth
        viewHolder.cityAndCountryTextView.text = "${patient.city} - ${patient.country}"
        viewHolder.medicalConditionsTextView.text = patient.medicalConditions

        return view
    }

    override fun getItem(position: Int): Any {
        return patients[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return patients.size
    }

    class ViewHolder(view: View) {

        val fullNameTextView : TextView = view.findViewById(R.id.fullNameTextView)
        val cardNumberTextView : TextView = view.findViewById(R.id.cardNumberTextView)
        val phoneNumberTextView : TextView = view.findViewById(R.id.phoneNumberTextView)
        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        val dateOfBirthTextView: TextView = view.findViewById(R.id.dateOfBirthTextView)
        val cityAndCountryTextView: TextView = view.findViewById(R.id.cityAndCountryTextView)
        val medicalConditionsTextView: TextView = view.findViewById(R.id.medicalConditionsTextView)
        val arrowButton: ImageButton = view.findViewById(R.id.expandButton)

        val cardNumberLayout  : LinearLayout = view.findViewById(R.id.cardNumberLayout)
        val phoneNumberLayout  : LinearLayout = view.findViewById(R.id.phoneNumberLayout)
        val emailLayout : LinearLayout = view.findViewById(R.id.emailLayout)
        val dateOfBirthLayout : LinearLayout = view.findViewById(R.id.dateOfBirthLayout)
        val cityAndCountryLayout : LinearLayout = view.findViewById(R.id.cityAndCountryLayout)
        val medicalConditionsLayout : LinearLayout = view.findViewById(R.id.medicalConditionsLayout)

        var areFieldsVisible = true

        init {
            arrowButton.setOnClickListener {
                areFieldsVisible = !areFieldsVisible
                updateVisibility()
            }
        }

         private fun updateVisibility() {
             if(areFieldsVisible) {
                 cardNumberLayout.visibility = View.VISIBLE
                 phoneNumberLayout.visibility = View.VISIBLE
                 emailLayout.visibility = View.VISIBLE
                 dateOfBirthLayout.visibility = View.VISIBLE
                 cityAndCountryLayout.visibility = View.VISIBLE
                 medicalConditionsLayout.visibility = View.VISIBLE
                 arrowButton.setImageResource(R.drawable.baseline_expand_less_24)
             }
             else {
                 cardNumberLayout.visibility = View.GONE
                 phoneNumberLayout.visibility = View.GONE
                 emailLayout.visibility = View.GONE
                 dateOfBirthLayout.visibility = View.GONE
                 cityAndCountryLayout.visibility = View.GONE
                 medicalConditionsLayout.visibility = View.GONE
                 arrowButton.setImageResource(R.drawable.baseline_expand_more_24)
             }
        }
    }
}