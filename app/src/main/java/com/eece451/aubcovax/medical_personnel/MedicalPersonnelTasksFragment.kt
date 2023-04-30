package com.eece451.aubcovax.medical_personnel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.DoseModel
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalPersonnelTasksFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var tasks : ArrayList<DoseModel>? = ArrayList()
    private var adapter : MedicalPersonnelTasksAdapter? = null

    private var tasksPlaceHolder: TextView? = null

    override fun onResume() {
        super.onResume()
        tasks?.clear()
        adapter?.notifyDataSetChanged()
        fillTasksList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.medical_personnel_tasks_fragment, container, false)

        tasksPlaceHolder = view.findViewById(R.id.tasksPlaceHolder)
        tasksPlaceHolder?.visibility = View.GONE

        listview = view.findViewById(R.id.tasksListView)
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
                        for(task in response.body()!!) {
                            if(task.status != "confirmed") {
                                tasks?.add(task)
                            }
                        }
                        if(tasks?.size == 0) {
                            tasksPlaceHolder?.visibility = View.VISIBLE
                        }
                        else {
                            tasksPlaceHolder?.visibility = View.GONE
                        }
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