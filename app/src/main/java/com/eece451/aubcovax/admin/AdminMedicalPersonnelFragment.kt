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
import com.eece451.aubcovax.api.Authentication.logout
import com.eece451.aubcovax.api.models.MedicalPersonnelModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMedicalPersonnelFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var medicalPersonnel : ArrayList<MedicalPersonnelModel>? = ArrayList()
    private var adapter : AdminMedicalPersonnelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillMedicalPersonnelList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.admin_medical_personnel_fragment, container, false)

        listview = view.findViewById(R.id.medicalPersonnelListView)
        adapter = AdminMedicalPersonnelAdapter(layoutInflater, medicalPersonnel!!)
        listview?.adapter = adapter

        return view
    }

    private fun fillMedicalPersonnelList() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getAllMedicalPersonnel("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<MedicalPersonnelModel>> {

                override fun onResponse(call: Call<List<MedicalPersonnelModel>>,
                                        response: Response<List<MedicalPersonnelModel>>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        medicalPersonnel?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                    else {
                        Snackbar.make(
                            requireView(),
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if(response.code() == 401 || response.code() == 403) {
                            logout(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<List<MedicalPersonnelModel>>, t: Throwable) {
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