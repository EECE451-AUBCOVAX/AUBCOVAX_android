package com.eece451.aubcovax.admin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.eece451.aubcovax.MainActivity
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.Authentication

class AdminMeFragment : Fragment() {

    private var logoutButton : Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View =  inflater.inflate(R.layout.admin_me_fragment, container, false)

        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { _ -> logout() }

        return view
    }

    private fun logout() {
        Authentication.clearRole()
        Authentication.clearToken()

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}