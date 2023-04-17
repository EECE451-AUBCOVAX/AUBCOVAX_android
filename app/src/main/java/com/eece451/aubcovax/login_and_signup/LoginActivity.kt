package com.eece451.aubcovax.login_and_signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eece451.aubcovax.MainActivity
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.LoginResponseModel
import com.eece451.aubcovax.api.models.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(loginButton?.windowToken, 0)

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

        val user = UserModel(username, password)

        loginButton?.isEnabled = false

        AUBCOVAXService.AUBCOVAXApi().login(user).enqueue(object: Callback<LoginResponseModel> {

            override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                if(response.isSuccessful) {
                    response.body()?.role?.let { Authentication.saveRole(it) }
                    response.body()?.token?.let { Authentication.saveToken(it) }

                    val intent = Intent(loginButton?.context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
                else {
                    if(response.code() == 401 || response.code() == 403) {
                        Snackbar.make(
                            loginButton as View,
                            "Wrong Credentials!",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else {
                        Snackbar.make(
                            loginButton as View,
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                loginButton?.isEnabled = true
            }

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                Snackbar.make(
                    loginButton as View,
                    t.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
                loginButton?.isEnabled = true
            }

        })
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