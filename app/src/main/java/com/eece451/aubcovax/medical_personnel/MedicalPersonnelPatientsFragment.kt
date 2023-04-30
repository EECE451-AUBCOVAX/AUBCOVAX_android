package com.eece451.aubcovax.medical_personnel

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MedicalPersonnelPatientsFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var listview : ListView? = null
    private var patients : ArrayList<PatientModel>? = ArrayList()
    private var originalPatientsList : ArrayList<PatientModel>? = ArrayList()
    private var filteredList : ArrayList<PatientModel>? = ArrayList()
    private var adapter : MedicalPersonnelPatientsAdapter? = null
    private var filter: Filter? = null
    private var searchView : SearchView? = null

    override fun onResume() {
        super.onResume()
        originalPatientsList?.clear()
        patients?.clear()
        adapter?.notifyDataSetChanged()
        fillPatientsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.medical_personnel_patients_fragment, container,
            false)

        filter = createFilter()

        adapter = MedicalPersonnelPatientsAdapter(layoutInflater, patients!!)

        listview = view.findViewById(R.id.patientsListView)
        listview?.adapter = adapter
        listview?.setOnItemClickListener { _, view, position, _ ->
            var selectedPatient = patients!![position]
            if(!searchView?.query.isNullOrEmpty()) {
                selectedPatient = filteredList!![position]
            }
            val intent = Intent(requireContext(), MedicalPersonnelPatientItemActivity::class.java)
            intent.putExtra("patient", selectedPatient)
            startActivity(intent)
        }

        searchView = view.findViewById(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filter?.filter(it)
                }
                return true
            }
        })

        return view
    }

    private fun createFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                patients = originalPatientsList
                filteredList?.clear()
                constraint?.let {
                    val searchQuery = it.toString().lowercase(Locale.getDefault())
                    patients?.forEach { item ->
                        if (item.firstName?.lowercase(Locale.getDefault())?.contains(searchQuery) == true
                            || item.phoneNumber?.lowercase(Locale.getDefault())?.contains(searchQuery) == true) {
                            filteredList?.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (constraint.isNullOrEmpty()) {
                    adapter?.setData(originalPatientsList!!)
                }
                else {
                    val resultList = results?.values as ArrayList<PatientModel>
                    adapter?.setData(resultList)
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun fillPatientsList() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            Authentication.logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getAllPatients("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<List<PatientModel>> {

                override fun onResponse(call: Call<List<PatientModel>>, response: Response<List<PatientModel>>
                ) {
                    progressBarManager.hideProgressBar()
                    if (response.isSuccessful) {
                        originalPatientsList?.addAll(response.body()!!)
                        patients?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    } else {
                        Snackbar.make(
                            requireView(),
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if (response.code() == 401 || response.code() == 403) {
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