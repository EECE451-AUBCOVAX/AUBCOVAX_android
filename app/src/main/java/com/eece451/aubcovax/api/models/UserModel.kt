package com.eece451.aubcovax.api.models

import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("user_name")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    constructor(
        username: String?,
        password: String?
    ) {
        this.username = username
        this.password = password
    }
}