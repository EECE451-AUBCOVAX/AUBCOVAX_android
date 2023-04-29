package com.eece451.aubcovax.medical_personnel

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

class MedicalPersonnelTasksFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var tasks : ArrayList<DoseModel>? = ArrayList()
    private var adapter : MedicalPersonnelTasksAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillTasksList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.admin_medical_personnel_fragment, container, false)

        listview = view.findViewById(R.id.medicalPersonnelListView)
        adapter = MedicalPersonnelTasksAdapter(layoutInflater, tasks!!)
        listview?.adapter = adapter

        return view
    }

    private fun fillTasksList() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            Authentication.logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getPersonnelAssignedDoses("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<DoseModel>> {

                override fun onResponse(call: Call<List<DoseModel>>,
                                        response: Response<List<DoseModel>>
                ) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        tasks?.addAll(response.body()!!)
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