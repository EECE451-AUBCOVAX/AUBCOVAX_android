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
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.models.PatientModel
import java.util.*
import kotlin.collections.ArrayList

class MedicalPersonnelPatientsFragment : Fragment() {

    private var listview : ListView? = null
    private var patients : ArrayList<PatientModel>? = ArrayList()
    private var originalPatientsList : ArrayList<PatientModel>? = ArrayList()
    private var adapter : MedicalPersonnelPatientsAdapter? = null
    private var filter: Filter? = null
    private var searchView : SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        for (i in 1 .. 9) {
            patients?.add(
                PatientModel(i, "Patient $i", "$i", "0$i/123456",
                "patient$i@email.com", "0$i-01-2023",
                "Country $i, City $i", "Medical Conditions $i")
            )
        }

        originalPatientsList?.addAll(patients!!)

        adapter?.notifyDataSetChanged()
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
            val selectedPatient = patients!![position]
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
                val filteredList = ArrayList<PatientModel>()
                constraint?.let {
                    val searchQuery = it.toString().lowercase(Locale.getDefault())
                    patients?.forEach { item ->
                        if (item.name?.lowercase(Locale.getDefault())?.contains(searchQuery) == true
                            || item.phoneNumber?.lowercase(Locale.getDefault())?.contains(searchQuery) == true) {
                            filteredList.add(item)
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
}