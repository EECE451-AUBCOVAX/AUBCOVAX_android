package com.eece451.aubcovax.api

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.startActivity
import com.eece451.aubcovax.MainActivity

object Authentication {

    private var token: String? = null
    private var role: String? = null
    private var preferences: SharedPreferences? = null

    fun initialize(context: Context) {
        preferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        token = preferences?.getString("TOKEN", null)
        role = preferences?.getString("ROLE", null)
    }

    fun getToken(): String? {
        return token
    }

    fun getRole(): String? {
        return role
    }

    fun saveToken(token: String) {
        this.token = token
        preferences?.edit()?.putString("TOKEN", token)?.apply()
    }

    fun saveRole(role: String) {
        this.role = role
        preferences?.edit()?.putString("ROLE", role)?.apply()
    }

    fun clearToken() {
        this.token = null
        preferences?.edit()?.remove("TOKEN")?.apply()
    }

    fun clearRole() {
        this.role = null
        preferences?.edit()?.remove("ROLE")?.apply()
    }

    fun logout(context: Context?) {
        clearRole()
        clearToken()

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context?.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
}