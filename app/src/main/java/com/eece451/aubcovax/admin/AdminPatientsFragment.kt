package com.eece451.aubcovax.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminPatientsFragment : Fragment() {

    private var listview : ListView? = null
    private var patients : ArrayList<PatientModel>? = ArrayList()
    private var adapter : AdminPatientsAdapter? = null

    private val progressBarManager = ProgressBarManager()

    override fun onResume() {
        super.onResume()
        patients?.clear()
        adapter?.notifyDataSetChanged()
        fillPatientsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.admin_patients_fragment, container, false)

        listview = view.findViewById(R.id.patientsListView)
        adapter = AdminPatientsAdapter(layoutInflater, patients!!)
        listview?.adapter = adapter

        return view
    }

    private fun fillPatientsList() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            Authentication.logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getAllPatients("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<PatientModel>> {

                override fun onResponse(call: Call<List<PatientModel>>, response: Response<List<PatientModel>>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        patients?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                    else {
                        Snackbar.make(
                            requireView(),
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if(response.code() == 401 || response.code() == 403) {
                            Authentication.logout(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<List<PatientModel>>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        requireView(),
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }
}