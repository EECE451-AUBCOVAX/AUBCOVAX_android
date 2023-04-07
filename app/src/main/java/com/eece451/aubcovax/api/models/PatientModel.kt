package com.eece451.aubcovax.api.models

import java.io.Serializable

class PatientModel : Serializable {

    var id : Int? = null
    var name : String? = null
    var idCardNumber : String? = null
    var phoneNumber : String? = null
    var email: String? = null
    var dateOfBirth : String? = null
    var cityAndCountry : String? = null
    var medicalConditions : String? = null

    constructor(
        id: Int?,
        name: String?,
        idCardNumber: String?,
        phoneNumber: String?,
        email: String?,
        dateOfBirth: String?,
        cityAndCountry: String?,
        medicalConditions: String?
    ) {
        this.id = id
        this.name = name
        this.idCardNumber = idCardNumber
        this.phoneNumber = phoneNumber
        this.email = email
        this.dateOfBirth = dateOfBirth
        this.cityAndCountry = cityAndCountry
        this.medicalConditions = medicalConditions
    }
}