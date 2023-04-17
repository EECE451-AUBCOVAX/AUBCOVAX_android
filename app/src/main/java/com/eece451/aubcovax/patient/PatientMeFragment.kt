package com.eece451.aubcovax.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.Authentication.logout
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientMeFragment : Fragment() {

    private var fullNameTextView : TextView? = null
    private var cardNumberTextView : TextView? = null
    private var phoneNumberTextView : TextView? = null
    private var emailTextView : TextView? = null
    private var dateOfBirthTextView : TextView? = null
    private var cityAndCountryTextView : TextView? = null
    private var medicalConditionsTextView : TextView? = null
    private var logoutButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPatientInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.patient_me_fragment, container, false)

        fullNameTextView = view.findViewById(R.id.fullNameTextView)
        cardNumberTextView = view.findViewById(R.id.cardNumberTextView)
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        dateOfBirthTextView = view.findViewById(R.id.dateOfBirthTextView)
        cityAndCountryTextView = view.findViewById(R.id.cityAndCountryTextView)
        medicalConditionsTextView = view.findViewById(R.id.medicalConditionsTextView)

        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { _ -> logout(requireContext()) }

        return view
    }

    private fun getPatientInfo() {
        if (Authentication.getToken() == null) {
            logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getUserInfo("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<PatientModel> {

                override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                    if(response.isSuccessful) {
                        fullNameTextView?.text = "${response.body()?.firstName} ${response.body()?.lastName}"
                        cardNumberTextView?.text = response.body()?.idCardNumber
                        phoneNumberTextView?.text = response.body()?.phoneNumber
                        emailTextView?.text = response.body()?.email
                        dateOfBirthTextView?.text = response.body()?.dateOfBirth
                        cityAndCountryTextView?.text = "${response.body()?.city} - ${response.body()?.country}"
                        medicalConditionsTextView?.text = response.body()?.medicalConditions
                    }
                    else {
                        Snackbar.make(
                            logoutButton as View,
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if(response.code() == 401 || response.code() == 403) {
                            logout(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                    Snackbar.make(
                        logoutButton as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }
}