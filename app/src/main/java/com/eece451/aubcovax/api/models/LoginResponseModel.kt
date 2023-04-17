package com.eece451.aubcovax.api.models

import com.google.gson.annotations.SerializedName

class LoginResponseModel {

    @SerializedName("role")
    var role: String? = null

    @SerializedName("token")
    var token: String? = null

}