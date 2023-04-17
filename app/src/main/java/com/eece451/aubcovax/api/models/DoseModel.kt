package com.eece451.aubcovax.api.models

import com.google.gson.annotations.SerializedName

class DoseModel {

    @SerializedName("date")
    var date : String? = null

    @SerializedName("time")
    var time: String? = null

    @SerializedName("personel")
    var medicalPersonnelName: String? = null

    @SerializedName("status")
    var status: String? = null

    constructor(date: String?, time: String?, medicalPersonnelName: String?, status: String?) {
        this.date = date
        this.time = time
        this.medicalPersonnelName = medicalPersonnelName
        this.status = status
    }
}