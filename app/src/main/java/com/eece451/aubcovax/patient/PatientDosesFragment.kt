package com.eece451.aubcovax.patient

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
import com.eece451.aubcovax.api.models.DoseModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDosesFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var doses : ArrayList<DoseModel>? = ArrayList()
    private var adapter : PatientDosesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillDosesList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.patient_doses_fragment, container, false)

        listview = view.findViewById(R.id.patientDosesListView)
        adapter = PatientDosesAdapter(layoutInflater, doses!!)
        listview?.adapter = adapter

        return view
    }

    private fun fillDosesList() {
        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            Authentication.logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getMyDoses("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<DoseModel>> {

                override fun onResponse(call: Call<List<DoseModel>>,
                                        response: Response<List<DoseModel>>
                ) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        doses?.addAll(response.body()!!)
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

                override fun onFailure(call: Call<List<DoseModel>>, t: Throwable) {
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