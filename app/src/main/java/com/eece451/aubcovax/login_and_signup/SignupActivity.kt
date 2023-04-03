package com.eece451.aubcovax.login_and_signup

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import com.eece451.aubcovax.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()

    private var inputIsValid = true
    private var firstNameEditText : TextInputLayout? = null
    private var lastNameEditText : TextInputLayout? = null
    private var dateOfBirthEditText : TextInputLayout? = null
    private var idCarNumberEditText : TextInputLayout? = null
    private var phoneNumberEditText : TextInputLayout? = null
    private var emailEditText : TextInputLayout? = null
    private var cityEditText : TextInputLayout? = null
    private var countryEditText : TextInputLayout? = null
    private var medicalConditionsEditText : TextInputLayout? = null
    private var usernameEditText : TextInputLayout? = null
    private var passwordEditText : TextInputLayout? = null
    private var confirmPasswordEditText : TextInputLayout? = null
    private var signupButton : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        firstNameEditText = findViewById(R.id.firstNameLayout)
        firstNameEditText?.setTextChangeListener()

        lastNameEditText = findViewById(R.id.lastNameLayout)
        lastNameEditText?.setTextChangeListener()

        dateOfBirthEditText = findViewById(R.id.dateOfBirthLayout)
        dateOfBirthEditText?.editText?.setOnClickListener { _ -> showDatePickerDialog() }
        dateOfBirthEditText?.setTextChangeListener()

        idCarNumberEditText = findViewById(R.id.idCardNumberLayout)
        idCarNumberEditText?.setTextChangeListener()

        phoneNumberEditText = findViewById(R.id.phoneNumberLayout)
        phoneNumberEditText?.setTextChangeListener()

        emailEditText = findViewById(R.id.emailLayout)
        emailEditText?.setTextChangeListener()

        cityEditText = findViewById(R.id.cityLayout)
        cityEditText?.setTextChangeListener()

        countryEditText = findViewById(R.id.countryLayout)
        countryEditText?.setTextChangeListener()

        medicalConditionsEditText = findViewById(R.id.medicalConditionsLayout)
        medicalConditionsEditText?.setTextChangeListener()

        usernameEditText = findViewById(R.id.usernameLayout)
        usernameEditText?.setTextChangeListener()

        passwordEditText = findViewById(R.id.passowrdLayout)
        passwordEditText?.setTextChangeListener()

        confirmPasswordEditText = findViewById(R.id.confirmPasswordLayout)
        confirmPasswordEditText?.setTextChangeListener()

        signupButton = findViewById(R.id.signupButton)
        signupButton?.setOnClickListener { _ -> signup() }

    }

    private fun TextInputLayout.setTextChangeListener() {
        this.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                this@setTextChangeListener.error = null
            }
        })
    }

    private fun showDatePickerDialog() {

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val dateOfBirth = calendar.time
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                dateOfBirthEditText?.editText?.setText(dateFormat.format(dateOfBirth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun signup() {

        val firstName = firstNameEditText?.editText?.text.toString()
        val lastName = lastNameEditText?.editText?.text.toString()
        val dateOfBirth = dateOfBirthEditText?.editText?.text.toString()
        val idCarNumber = idCarNumberEditText?.editText?.text.toString()
        val  phoneNumber = phoneNumberEditText?.editText?.text.toString()
        val email = emailEditText?.editText?.text.toString()
        val city = cityEditText?.editText?.text.toString()
        val country = countryEditText?.editText?.text.toString()
        val medicalConditions = medicalConditionsEditText?.editText?.text.toString()
        val username = usernameEditText?.editText?.text.toString()
        val password = passwordEditText?.editText?.text.toString()
        val confirmPassword = confirmPasswordEditText?.editText?.text.toString()

        validate(firstName, firstNameEditText, "Please enter your first name")
        validate(lastName, lastNameEditText, "Please enter your last name")
        validate(dateOfBirth, dateOfBirthEditText, "Please enter your date of birth")
        validate(idCarNumber, idCarNumberEditText, "Please enter your id card number")
        validate(phoneNumber, phoneNumberEditText, "Please enter your phone number")
        validate(email, emailEditText, "Please enter your email")
        validate(city, cityEditText, "Please enter your city")
        validate(country, countryEditText, "Please enter your country")
        validate(medicalConditions, medicalConditionsEditText, "Please enter your medical conditions")
        validate(username, usernameEditText, "Please enter your username")
        validate(password, passwordEditText, "Please enter your password")
        validateConfirmedPassword(password, confirmPassword, confirmPasswordEditText,
            "Passwords do not match")

        if(!inputIsValid) {
            return
        }
    }

    private fun validate(input : String, textInputLayout : TextInputLayout?, errorMessage : String)
    {
        if (input.isBlank()) {
            inputIsValid = false
            textInputLayout?.error = errorMessage
        }
    }

    private fun validateConfirmedPassword(password : String, confirmedPassword : String,
                                          textInputLayout : TextInputLayout?, errorMessage : String)
    {
        if (password != confirmedPassword) {
            inputIsValid = false
            textInputLayout?.error = errorMessage
        }
    }
}