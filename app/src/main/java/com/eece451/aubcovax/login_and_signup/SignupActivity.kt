package com.eece451.aubcovax.login_and_signup

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.eece451.aubcovax.MainActivity
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.LoginResponseModel
import com.eece451.aubcovax.api.models.PatientModel
import com.eece451.aubcovax.api.models.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
class SignupActivity : AppCompatActivity() {

    private val progressBarManager = ProgressBarManager()
    private val calendar = Calendar.getInstance()

    private var inputIsValid = true
    private var firstNameEditText : TextInputLayout? = null
    private var lastNameEditText : TextInputLayout? = null
    private var dateOfBirthEditText : TextInputLayout? = null
    private var idCardNumberEditText : TextInputLayout? = null
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

        idCardNumberEditText = findViewById(R.id.idCardNumberLayout)
        idCardNumberEditText?.setTextChangeListener()

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
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(signupButton?.windowToken, 0)

        val firstName = firstNameEditText?.editText?.text.toString()
        val lastName = lastNameEditText?.editText?.text.toString()
        val dateOfBirth = dateOfBirthEditText?.editText?.text.toString()
        val idCardNumber = idCardNumberEditText?.editText?.text.toString()
        val phoneNumber = phoneNumberEditText?.editText?.text.toString()
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
        validate(idCardNumber, idCardNumberEditText, "Please enter your id card number")
        validate(phoneNumber, phoneNumberEditText, "Please enter your phone number")
        validateEmail(email, emailEditText)
        validate(city, cityEditText, "Please enter your city")
        validate(country, countryEditText, "Please enter your country")
        validate(medicalConditions, medicalConditionsEditText, "Please enter your medical conditions")
        validate(username, usernameEditText, "Please enter your username")
        validate(password, passwordEditText, "Please enter your password")
        validateConfirmedPassword(password, confirmPassword, confirmPasswordEditText)

        if(!inputIsValid) {
            return
        }

        val patient = PatientModel(
            firstName = firstName,
            lastName = lastName,
            idCardNumber = idCardNumber,
            phoneNumber = phoneNumber,
            email = email,
            username = username,
            password = password,
            dateOfBirth = dateOfBirth,
            city = city,
            country = country,
            medicalConditions = medicalConditions
        )

        signupButton?.isEnabled = false
        progressBarManager.showProgressBar(this)

        AUBCOVAXService.AUBCOVAXApi().createPatientAccount(patient).enqueue(object: Callback<PatientModel> {

            override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                if(response.isSuccessful) {
                    loginAndReserveFirstAppointment(UserModel(username, password))
                }
                else {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        signupButton as View,
                        response.errorBody()?.string().toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                signupButton?.isEnabled = true
            }

            override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                progressBarManager.hideProgressBar()
                Snackbar.make(
                    signupButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
                signupButton?.isEnabled = true
            }

        })
    }

    private fun validate(input : String, textInputLayout : TextInputLayout?, errorMessage : String) {
        if (input.isBlank()) {
            inputIsValid = false
            textInputLayout?.error = errorMessage
        }
    }

    private fun validateEmail(email: String, textInputLayout : TextInputLayout?) {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        if(!emailRegex.matches(email)) {
            inputIsValid = false
            textInputLayout?.error = "Please enter a valid email"
        }
    }

    private fun validateConfirmedPassword(password : String, confirmedPassword : String,
                                          textInputLayout : TextInputLayout?) {
        if (password != confirmedPassword) {
            inputIsValid = false
            textInputLayout?.error = "Passwords do not match"
        }
    }

    private fun loginAndReserveFirstAppointment(user: UserModel) {

        AUBCOVAXService.AUBCOVAXApi().login(user).enqueue(object: Callback<LoginResponseModel> {

            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {


                if(response.isSuccessful) {

                    response.body()?.role?.let { Authentication.saveRole(it) }
                    response.body()?.token?.let { Authentication.saveToken(it) }

                    AUBCOVAXService.AUBCOVAXApi().reserveFirstAppointment(
                        "Bearer ${Authentication.getToken()}"
                    ).enqueue(object: Callback<Any>{

                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            val intent = Intent(signupButton?.context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {}
                    })
                }
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {}
        })
    }

}