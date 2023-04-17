package com.eece451.aubcovax.api.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PatientModel : Serializable {

    @SerializedName("user_name")
    var username : String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("id_card")
    var idCardNumber: String? = null

    @SerializedName("phone_number")
    var phoneNumber: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("date_of_birth")
    var dateOfBirth: String? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("medical_conditions")
    var medicalConditions: String? = null

    constructor(
        firstName: String?,
        lastName: String?,
        idCardNumber: String?,
        phoneNumber: String?,
        email: String?,
        username: String?,
        password: String?,
        dateOfBirth: String?,
        city: String?,
        country: String?,
        medicalConditions: String?
    ) {
        this.firstName = firstName
        this.lastName = lastName
        this.idCardNumber = idCardNumber
        this.phoneNumber = phoneNumber
        this.email = email
        this.username = username
        this.password = password
        this.dateOfBirth = dateOfBirth
        this.city = city
        this.country = country
        this.medicalConditions = medicalConditions
    }
}