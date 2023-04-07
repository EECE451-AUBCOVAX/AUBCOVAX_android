package com.eece451.aubcovax.login_and_signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eece451.aubcovax.MainActivity
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.Authentication
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private var usernameEditText: TextInputLayout? = null
    private var passwordEditText: TextInputLayout? = null
    private var loginButton : Button? = null
    private var linkToSignUpTexView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        usernameEditText = findViewById(R.id.usernameLayout)
        usernameEditText?.setTextChangeListener()

        passwordEditText = findViewById(R.id.passwordLayout)
        passwordEditText?.setTextChangeListener()

        loginButton = findViewById(R.id.btnLogin)
        loginButton?.setOnClickListener { _ -> login() }

        linkToSignUpTexView = findViewById(R.id.textViewLinkToSignup)
        linkToSignUpTexView?.setOnClickListener { _ -> startSignupActivity() }
        val linkToSignUp = SpannableString("Don't have an account? Register now!")
        linkToSignUp.setSpan(UnderlineSpan(), 0, linkToSignUp.length, 0)
        linkToSignUpTexView?.text = linkToSignUp
    }

    private fun login() {

        val username = usernameEditText?.editText?.text?.toString()
        val password = passwordEditText?.editText?.text?.toString()

        if (username == null || password == null) {
            return
        }

        var inputIsValid = true
        if (username.isBlank()) {
            inputIsValid = false
            usernameEditText?.error = "Please enter your username"
        }
        if (password.isBlank()) {
            inputIsValid = false
            passwordEditText?.error = "Please enter your password"
        }
        if (!inputIsValid) {
            return
        }

        if (username == "patient" && password == "patient") {
            Authentication.saveRole("patient")
        }
        else if (username == "admin" && password == "admin") {
            Authentication.saveRole("admin")
        }
        else if (username == "staff" && password == "staff") {
            Authentication.saveRole("medical_personnel")
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun startSignupActivity() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
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
}