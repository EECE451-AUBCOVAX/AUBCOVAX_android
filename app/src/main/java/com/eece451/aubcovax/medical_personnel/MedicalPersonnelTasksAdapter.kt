package com.eece451.aubcovax.medical_personnel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.DoseModel

class MedicalPersonnelTasksAdapter (private val inflater: LayoutInflater,
                                    private val dataSource: List<DoseModel>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.patient_dose_list_item, parent, false)

        val date: String = dataSource[position].date.toString()
        val time: String? = dataSource[position].time

        view.findViewById<TextView>(R.id.titleTextView)?.visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.doseAdministeredByLayout)?.visibility = View.GONE
        view.findViewById<LinearLayout>(R.id.patientNameLayout)?.visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.patientTextView).text = dataSource[position].patientName

        view.findViewById<TextView>(R.id.doseDateAndTimeTextView).text = "$date $time"

        view.findViewById<TextView>(R.id.doseAdministeredByTextView)?.visibility = View.GONE

        view.findViewById<ImageView>(R.id.doseUpcomingIcon)?.visibility = View.GONE
        view.findViewById<ImageView>(R.id.doseConfirmedIcon)?.visibility = View.GONE

        view.findViewById<TextView>(R.id.doseUpcomingMessage)?.visibility = View.GONE
        view.findViewById<TextView>(R.id.doseConfirmedMessage)?.visibility = View.GONE

        view.findViewById<Button>(R.id.confirmButton)?.visibility = View.GONE

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
