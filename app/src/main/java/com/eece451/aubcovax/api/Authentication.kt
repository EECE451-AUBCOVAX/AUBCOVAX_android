package com.eece451.aubcovax.api

import android.content.Context
import android.content.SharedPreferences

object Authentication {

    private var token: String? = null
    private var role: String? = null
    private var preferences: SharedPreferences? = null

    public fun initialize(context: Context) {
        preferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        token = preferences?.getString("TOKEN", null)
        role = preferences?.getString("ROLE", null)
    }

    public fun getToken(): String? {
        return token
    }

    public fun getRole(): String? {
        return role
    }

    public fun saveToken(token: String) {
        this.token = token
        preferences?.edit()?.putString("TOKEN", token)?.apply()
    }

    public fun saveRole(role: String) {
        this.role = role
        preferences?.edit()?.putString("ROLE", role)?.apply()
    }

    public fun clearToken() {
        this.token = null
        preferences?.edit()?.remove("TOKEN")?.apply()
    }

    public fun clearRole() {
        this.role = null
        preferences?.edit()?.remove("ROLE")?.apply()
    }

}