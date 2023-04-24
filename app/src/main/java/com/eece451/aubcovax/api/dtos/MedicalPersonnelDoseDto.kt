package com.eece451.aubcovax.api.dtos

import com.google.gson.annotations.SerializedName

class MedicalPersonnelDoseDto {

    @SerializedName("patient")
    var patientUsername: String? = null

    @SerializedName("date")
    var date: String? = null

    @SerializedName("time")
    var time: String? = null

    constructor(patientUsername: String?, date: String?, time: String?) {
        this.patientUsername = patientUsername
        this.date = date
        this.time = time
    }
}