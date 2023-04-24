package com.eece451.aubcovax.medical_personnel

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.dtos.MedicalPersonnelDoseDto
import com.eece451.aubcovax.api.models.DoseModel
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MedicalPersonnelPatientItemActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener  {

    private val context: Context = this
    private var timeDialog: AlertDialog? = null
    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var doses : ArrayList<DoseModel>? = ArrayList()
    private var adapter : MedicalPersonnelDosesAdapter? = null

    var fullNameTextView: TextView? = null
    var cardNumberTextView: TextView? = null
    var phoneNumberTextView: TextView? = null
    var emailTextView: TextView? = null
    private var dateOfBirthTextView: TextView? = null
    private var cityAndCountryTextView: TextView? = null
    private var medicalConditionsTextView: TextView? = null

    private var bookAppointmentButton : Button? = null

    private var day = -1
    private var month = -1
    private var year = -1

    private var selectedDay = -1
    private var selectedMonth = -1
    private var selectedYear = -1
    private var selectedHour = -1
    private var selectedMinute = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_personnel_patient_item_activity)

        val patient = intent.getSerializableExtra("patient") as? PatientModel

        listview = findViewById(R.id.medicalPersonnelDosesListView)
        adapter = MedicalPersonnelDosesAdapter(this, layoutInflater, patient?.username, doses!!)
        listview?.adapter = adapter
        setListViewHeightBasedOnChildren(listview)

        fullNameTextView = findViewById(R.id.fullNameTextView)
        cardNumberTextView = findViewById(R.id.cardNumberTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        emailTextView = findViewById(R.id.emailTextView)
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView)
        cityAndCountryTextView = findViewById(R.id.cityAndCountryTextView)
        medicalConditionsTextView = findViewById(R.id.medicalConditionsTextView)


        fillPatientDosesList(patient?.username)
        fullNameTextView?.text = patient?.firstName
        cardNumberTextView?.text = patient?.idCardNumber
        phoneNumberTextView?.text = patient?.phoneNumber
        emailTextView?.text = patient?.email
        dateOfBirthTextView?.text = patient?.dateOfBirth
        cityAndCountryTextView?.text = patient?.city
        medicalConditionsTextView?.text = patient?.medicalConditions

        bookAppointmentButton = findViewById(R.id.bookAppointmentButton)
        bookAppointmentButton?.setOnClickListener {
            val calendar: Calendar = getCalendar()
            val datePickerDialog = DatePickerDialog(this, this, year, month, day)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        val hourPicker = getHourPicker()
        val minutePicker = getMinutePicker()

        timeDialog = AlertDialog.Builder(context)
            .setTitle("Set Appointment Time")
            .setView(R.layout.dialog_time_picker)
            .setPositiveButton("OK") { _, _ ->
                selectedHour = hourPicker.value
                selectedMinute = minutePicker.value * 30
                bookAppointment(patient?.username)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()

        timeDialog?.setOnShowListener {
            val hourPickerLayout = timeDialog?.findViewById<View>(R.id.hour_picker_layout) as LinearLayout
            val minutePickerLayout = timeDialog?.findViewById<View>(R.id.minute_picker_layout) as LinearLayout

            val hourPickerParent = hourPicker.parent as? ViewGroup
            hourPickerParent?.removeView(hourPicker)

            val minutePickerParent = minutePicker.parent as? ViewGroup
            minutePickerParent?.removeView(minutePicker)

            hourPickerLayout.addView(hourPicker)
            minutePickerLayout.addView(minutePicker)
        }
    }

    private fun getHourPicker(): NumberPicker {
        return NumberPicker(context).apply {
            minValue = 8
            maxValue = 16
            value = 8
        }
    }

    public fun restart() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    private fun getMinutePicker(): NumberPicker {
        return NumberPicker(context).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("0", "30")
        }
    }

    private fun getCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, 2)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        return calendar
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        selectedDay = day
        selectedMonth = month + 1
        selectedYear = year
        timeDialog?.show()
    }

    private fun bookAppointment(patientUsername: String?) {
        println("$selectedYear - $selectedMonth - $selectedDay at $selectedHour:$selectedMinute")
        println("##############################################################################################")

        val date = "$selectedYear-$selectedMonth-$selectedDay"
        var time = "$selectedHour:"
        time += if(selectedMinute == 0) {
            "00"
        }
        else {
            "30"
        }

        val dose = MedicalPersonnelDoseDto(patientUsername, date, time)

        progressBarManager.showProgressBar(this)

        AUBCOVAXService.AUBCOVAXApi().reserveSecondAppointment(
            "Bearer ${Authentication.getToken()}", dose)
            .enqueue(object: Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    println("#########################################################################")
                    println(response.body())
                    progressBarManager.hideProgressBar()

                    val intent = intent
                    finish()
                    startActivity(intent)

//                    if(response.isSuccessful) {
//                        val intent = intent
//                        finish()
//                        startActivity(intent)
//                    }
//                    else {
//                        if(response.code() == 401 || response.code() == 403) {
//                            Authentication.logout(context)
//                        }
//                        else {
//                            Snackbar.make(
//                                fullNameTextView as View,
//                               response.errorBody().toString(),
//                                Snackbar.LENGTH_LONG
//                            ).show()
//                        }
//                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun fillPatientDosesList(patientUsername: String?) {
        progressBarManager.showProgressBar(this)

        if (Authentication.getToken() == null) {
            Authentication.logout(context)
        }
        AUBCOVAXService.AUBCOVAXApi().getPatientDoses(
            "Bearer ${Authentication.getToken()}", patientUsername)
            .enqueue(object : Callback<List<DoseModel>> {

                override fun onResponse(call: Call<List<DoseModel>>,
                                        response: Response<List<DoseModel>>
                ) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        doses?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                        setListViewHeightBasedOnChildren(listview)
                    }
                    else {
                        Snackbar.make(
                            fullNameTextView as View,
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if(response.code() == 401 || response.code() == 403) {
                            Authentication.logout(context)
                        }
                    }
                }

                override fun onFailure(call: Call<List<DoseModel>>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView?) {
        val listAdapter = listView?.adapter ?: return
        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)
        for (i in 0 until listAdapter.count) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}