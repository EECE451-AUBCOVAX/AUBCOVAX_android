package com.eece451.aubcovax

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eece451.aubcovax.api.Authentication

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Authentication.initialize(this)

        val intent = when (Authentication.getRole()) {
//            "admin" -> Intent(this, AdminActivity::class.java)
//            "medical_personnel" -> Intent(this, MedicalPersonnelActivity::class.java)
//            "patient" -> Intent(this, PatientActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

}