package com.eece451.aubcovax.patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.DoseModel

class PatientDosesAdapter (private val inflater: LayoutInflater,
                           private val dataSource: List<DoseModel>)
    : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.patient_dose_list_item, parent, false)

        view.findViewById<LinearLayout>(R.id.doseAdministeredByLayout)?.visibility = View.VISIBLE
        view.findViewById<LinearLayout>(R.id.patientNameLayout)?.visibility = View.GONE

        view.findViewById<TextView>(R.id.doseAdministeredByTextView).text =
            "${dataSource[position].medicalPersonnelName}"
        view.findViewById<TextView>(R.id.doseDateAndTimeTextView).text =
            "${dataSource[position].date} ${dataSource[position].time}"

        view.findViewById<Button>(R.id.confirmButton).visibility = View.GONE

        if (dataSource[position].status == "confirmed") {
            view.findViewById<ImageView>(R.id.doseUpcomingIcon).visibility = View.GONE
            view.findViewById<ImageView>(R.id.doseConfirmedIcon).visibility = View.VISIBLE

            view.findViewById<TextView>(R.id.doseUpcomingMessage).visibility = View.GONE
            view.findViewById<TextView>(R.id.doseConfirmedMessage).visibility = View.VISIBLE
        }
        else {
            view.findViewById<ImageView>(R.id.doseUpcomingIcon).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.doseConfirmedIcon).visibility = View.GONE

            view.findViewById<TextView>(R.id.doseUpcomingMessage).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.doseConfirmedMessage).visibility = View.GONE
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}
