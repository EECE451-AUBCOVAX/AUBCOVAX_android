package com.eece451.aubcovax.medical_personnel

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.admin.AdminPatientsAdapter
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.DoseModel
import com.eece451.aubcovax.api.models.PatientModel
import com.eece451.aubcovax.patient.PatientDosesAdapter
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalPersonnelPatientItemActivity : AppCompatActivity() {

    private val context: Context = this

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var doses : ArrayList<DoseModel>? = ArrayList()
    private var adapter : MedicalPersonnelDosesAdapter? = null

    var fullNameTextView: TextView? = null
    var cardNumberTextView: TextView? = null
    var phoneNumberTextView: TextView? = null
    var emailTextView: TextView? = null
    var dateOfBirthTextView: TextView? = null
    var cityAndCountryTextView: TextView? = null
    var medicalConditionsTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_personnel_patient_item_activity)

        listview = findViewById(R.id.medicalPersonnelDosesListView)
        adapter = MedicalPersonnelDosesAdapter(layoutInflater, doses!!)
        listview?.adapter = adapter
        setListViewHeightBasedOnChildren(listview)

        fullNameTextView = findViewById(R.id.fullNameTextView)
        cardNumberTextView = findViewById(R.id.cardNumberTextView)
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView)
        emailTextView = findViewById(R.id.emailTextView)
        dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView)
        cityAndCountryTextView = findViewById(R.id.cityAndCountryTextView)
        medicalConditionsTextView = findViewById(R.id.medicalConditionsTextView)

        val patient = intent.getSerializableExtra("patient") as? PatientModel

        fillPatientDosesList(patient?.username)

        fullNameTextView?.text = patient?.firstName
        cardNumberTextView?.text = patient?.idCardNumber
        phoneNumberTextView?.text = patient?.phoneNumber
        emailTextView?.text = patient?.email
        dateOfBirthTextView?.text = patient?.dateOfBirth
        cityAndCountryTextView?.text = patient?.city
        medicalConditionsTextView?.text = patient?.medicalConditions

    }

    private fun fillPatientDosesList(patientUsername: String?) {
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