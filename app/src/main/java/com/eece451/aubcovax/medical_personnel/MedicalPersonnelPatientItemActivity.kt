package com.eece451.aubcovax.medical_personnel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.PatientModel

class MedicalPersonnelPatientItemActivity : AppCompatActivity() {

    val fullNameTextView: TextView? = null
    val cardNumberTextView: TextView? = null
    val phoneNumberTextView: TextView? = null
    val emailTextView: TextView? = null
    val dateOfBirthTextView: TextView? = null
    val cityAndCountryTextView: TextView? = null
    val medicalConditionsTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_personnel_patient_item_activity)

        val fullNameTextView: TextView = findViewById(R.id.fullNameTextView)
        val cardNumberTextView: TextView = findViewById(R.id.cardNumberTextView)
        val phoneNumberTextView: TextView = findViewById(R.id.phoneNumberTextView)
        val emailTextView: TextView = findViewById(R.id.emailTextView)
        val dateOfBirthTextView: TextView = findViewById(R.id.dateOfBirthTextView)
        val cityAndCountryTextView: TextView = findViewById(R.id.cityAndCountryTextView)
        val medicalConditionsTextView: TextView = findViewById(R.id.medicalConditionsTextView)

        val patient = intent.getSerializableExtra("patient") as? PatientModel

        fullNameTextView.text = patient?.firstName
        cardNumberTextView.text = patient?.idCardNumber
        phoneNumberTextView.text = patient?.phoneNumber
        emailTextView.text = patient?.email
        dateOfBirthTextView.text = patient?.dateOfBirth
        cityAndCountryTextView.text = patient?.city
        medicalConditionsTextView.text = patient?.medicalConditions
    }
}