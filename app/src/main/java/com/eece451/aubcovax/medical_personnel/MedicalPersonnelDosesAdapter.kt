package com.eece451.aubcovax.medical_personnel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.dtos.MedicalPersonnelDoseDto
import com.eece451.aubcovax.api.models.DoseModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MedicalPersonnelDosesAdapter (private val context: Context,
                                    private val inflater: LayoutInflater,
                                    private val patientUsername: String?,
                                    private val dataSource: List<DoseModel>)
    : BaseAdapter() {

    private var doseUpcomingIcon: ImageView? = null
    private var doseConfirmedIcon: ImageView? = null

    private var doseUpcomingMessage: TextView? = null
    private var doseConfirmedMessage: TextView? = null
    private var confirmButton: Button? = null

    private val adapter = this

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = inflater.inflate(R.layout.patient_dose_list_item, parent, false)

        val medicalPersonnelName: String? = dataSource[position].medicalPersonnelName
        val date: String = dataSource[position].date.toString()
        val time: String? = dataSource[position].time

        view.findViewById<LinearLayout>(R.id.patientNameLayout).visibility = View.GONE
        view.findViewById<TextView>(R.id.doseAdministeredByTextView).text = "$medicalPersonnelName"
        view.findViewById<TextView>(R.id.doseDateAndTimeTextView).text = "$date $time"

        doseUpcomingIcon = view.findViewById(R.id.doseUpcomingIcon)
        doseConfirmedIcon = view.findViewById(R.id.doseConfirmedIcon)

        doseUpcomingMessage = view.findViewById(R.id.doseUpcomingMessage)
        doseConfirmedMessage = view.findViewById(R.id.doseConfirmedMessage)

        confirmButton = view.findViewById(R.id.confirmButton)
        confirmButton?.setOnClickListener { _ ->
            confirmDose(patientUsername, date, time)
        }

        if (dataSource[position].status == "confirmed") {
            doseUpcomingIcon?.visibility = View.GONE
            doseConfirmedIcon?.visibility = View.VISIBLE

            doseUpcomingMessage?.visibility = View.GONE
            doseConfirmedMessage?.visibility = View.VISIBLE

            confirmButton?.visibility = View.GONE
        }
        else {
            doseUpcomingIcon?.visibility = View.VISIBLE
            doseConfirmedIcon?.visibility = View.GONE

            doseUpcomingMessage?.visibility = View.VISIBLE
            doseConfirmedMessage?.visibility = View.GONE

            confirmButton?.visibility = View.VISIBLE
        }

        return view
    }

    private fun confirmDose(patientUsername: String?, date: String, time: String?) {
        val timeWithSecondsFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeWithSeconds = time?.let { timeWithSecondsFormat.parse(it) }
        val timeWithoutSecondsFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeWithoutSeconds = timeWithSeconds?.let { timeWithoutSecondsFormat.format(it) }

        val dose = MedicalPersonnelDoseDto(patientUsername, date, timeWithoutSeconds)

        confirmButton?.isEnabled = false
        AUBCOVAXService.AUBCOVAXApi().confirmDose(
            "Bearer ${Authentication.getToken()}", dose).enqueue(object: Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {

                confirmButton?.isEnabled = true
                if(response.isSuccessful) {
                    val index = dataSource.indexOfFirst { it.date == date && it.time == time}
                    dataSource[index].status = "confirmed"
                    adapter.notifyDataSetChanged()
                }
                else {
                    if(response.code() == 401 || response.code() == 403) {
                        Authentication.logout(context)
                    }
                    else {
                        Snackbar.make(
                            confirmButton as View,
                            response.errorBody().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                confirmButton?.isEnabled = true
                Snackbar.make(
                    confirmButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        })
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
