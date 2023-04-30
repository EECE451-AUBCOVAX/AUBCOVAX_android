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
import com.eece451.aubcovax.api.dtos.ResponseMessageDto
import com.eece451.aubcovax.api.models.DoseModel
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MedicalPersonnelPatientItemActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener  {

    private val context: Context = this
    private var timeDialog: AlertDialog? = null
    private val patientDosesProgressBarManager = ProgressBarManager()
    private val personnelAssignedDosesProgressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var doses : ArrayList<DoseModel>? = ArrayList()
    private var personnelAssignedDoses : ArrayList<DoseModel>? = ArrayList()
    private var personnelAssignedDosesMap = HashMap<String, String>()
    private var adapter : MedicalPersonnelDosesAdapter? = null

    var fullNameTextView: TextView? = null
    var cardNumberTextView: TextView? = null
    var phoneNumberTextView: TextView? = null
    var emailTextView: TextView? = null
    private var dateOfBirthTextView: TextView? = null
    private var cityAndCountryTextView: TextView? = null
    private var medicalConditionsTextView: TextView? = null

    private var bookAppointmentButton : Button? = null

    private var calendar: Calendar? = null
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

        getPersonnelAssignedDoses()

        fillPatientDosesList(patient?.username)
        fullNameTextView?.text = patient?.firstName
        cardNumberTextView?.text = patient?.idCardNumber
        phoneNumberTextView?.text = patient?.phoneNumber
        emailTextView?.text = patient?.email
        dateOfBirthTextView?.text = patient?.dateOfBirth
        cityAndCountryTextView?.text = patient?.city
        medicalConditionsTextView?.text = patient?.medicalConditions

        bookAppointmentButton = findViewById(R.id.bookAppointmentButton)
        bookAppointmentButton?.visibility = View.GONE
        bookAppointmentButton?.setOnClickListener {
            calendar = getCalendar()
            val datePickerDialog = DatePickerDialog(this, this, year, month, day)
            datePickerDialog.datePicker.minDate = calendar?.timeInMillis!!
            datePickerDialog.show()
        }

        val hourPicker = getHourPicker()
        val minutePicker = getMinutePicker()

        timeDialog = AlertDialog.Builder(context)
            .setTitle("Set Appointment Time")
            .setView(R.layout.dialog_time_picker)
            .setPositiveButton("OK", null)
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

            val dialogView = timeDialog?.findViewById<View>(android.R.id.content)

            val positiveButton = timeDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener {
                selectedHour = hourPicker.value
                selectedMinute = minutePicker.value * 30

                val selectedDate : String = getSelectedDate()
                val selectedTime : String = getSelectedTime()

                val isAppointmentAvailable: Boolean = checkIfAppointmentIsAvailable(selectedDate, selectedTime)
                if(isAppointmentAvailable) {
                    bookAppointment(patient?.username, selectedDate, selectedTime)
                    timeDialog?.dismiss()
                }
                else {
                    Snackbar.make(
                        dialogView!!,
                        "An appointment is already booked on " + getSelectedDate() + " at " + getSelectedTime(),
                        800
                    ).show()
                }
            }
        }
    }

    private fun checkIfAppointmentIsAvailable(selectedDate: String, selectedTime: String): Boolean {
        return personnelAssignedDosesMap["$selectedDate-$selectedTime:00"] == null
    }

    private fun getHourPicker(): NumberPicker {
        return NumberPicker(context).apply {
            minValue = 8
            maxValue = 16
            value = 8
        }
    }

    private fun getMinutePicker(): NumberPicker {
        return NumberPicker(context).apply {
            minValue = 0
            maxValue = 1
            displayedValues = arrayOf("00", "30")
        }
    }

    private fun getCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd").parse(doses?.first()?.date)
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
        calendar?.set(year, month, day)

        if(calendar?.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
            calendar?.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Snackbar.make(
                bookAppointmentButton as View,
                "Can't book an appointment on the weekend",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            timeDialog?.show()
        }
    }

    private fun getSelectedTime() : String {
        var selectedTime = if(selectedHour < 10) "0$selectedHour" else "$selectedHour"
        selectedTime += (if(selectedMinute == 0) ":00" else ":30")
        return "$selectedTime"
    }

    private fun getSelectedDate() : String {
        return convertDateFormat("$selectedYear-$selectedMonth-$selectedDay")
    }

    private fun bookAppointment(patientUsername: String?, selectedDate: String, selectedTime: String) {

        val dose = MedicalPersonnelDoseDto(patientUsername, selectedDate, selectedTime)

        patientDosesProgressBarManager.showProgressBar(this)

        AUBCOVAXService.AUBCOVAXApi().reserveSecondAppointment(
            "Bearer ${Authentication.getToken()}", dose)
            .enqueue(object: Callback<ResponseMessageDto> {

                override fun onResponse(call: Call<ResponseMessageDto>, response: Response<ResponseMessageDto>) {

                    patientDosesProgressBarManager.hideProgressBar()

                    if(response.isSuccessful) {
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }
                    else {
                        if(response.code() == 401 || response.code() == 403) {
                            Authentication.logout(context)
                        }
                        else {
                            val errorResponse = Gson().fromJson(response.errorBody()!!.charStream(), ResponseMessageDto::class.java)
                            errorResponse.message?.let {
                                Snackbar.make(
                                    fullNameTextView as View,
                                    it,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseMessageDto>, t: Throwable) {
                    patientDosesProgressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun fillPatientDosesList(patientUsername: String?) {
        patientDosesProgressBarManager.showProgressBar(this)

        if (Authentication.getToken() == null) {
            Authentication.logout(context)
        }
        AUBCOVAXService.AUBCOVAXApi().getPatientDoses(
            "Bearer ${Authentication.getToken()}", patientUsername)
            .enqueue(object : Callback<List<DoseModel>> {

                override fun onResponse(call: Call<List<DoseModel>>,
                                        response: Response<List<DoseModel>>
                ) {
                    patientDosesProgressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        doses?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                        if(doses?.size == 2) {
                            bookAppointmentButton?.visibility = View.GONE
                        }
                        else {
                            bookAppointmentButton?.visibility = View.VISIBLE
                        }
                        setListViewHeightBasedOnChildren(listview)
                    }
                    else if(response.code() == 404) {
                        bookAppointmentButton?.visibility = View.GONE
                        Snackbar.make(
                            fullNameTextView as View,
                            "No reservations were found",
                            Snackbar.LENGTH_LONG
                        ).show()
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
                    patientDosesProgressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun getPersonnelAssignedDoses() {
        personnelAssignedDosesProgressBarManager.showProgressBar(this)

        if (Authentication.getToken() == null) {
            Authentication.logout(context)
        }
        AUBCOVAXService.AUBCOVAXApi().getPersonnelAssignedDoses(
            "Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<DoseModel>> {

                override fun onResponse(call: Call<List<DoseModel>>,
                                        response: Response<List<DoseModel>>
                ) {
                    personnelAssignedDosesProgressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        personnelAssignedDoses?.addAll(response.body()!!)
                        for (dose in personnelAssignedDoses!!) {
                            if(dose.status != "confirmed") {
                                val dateTime = "${dose.date}-${dose.time}"
                                personnelAssignedDosesMap[dateTime] = ""
                            }
                        }
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
                    personnelAssignedDosesProgressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun convertDateFormat(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate: Date = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
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