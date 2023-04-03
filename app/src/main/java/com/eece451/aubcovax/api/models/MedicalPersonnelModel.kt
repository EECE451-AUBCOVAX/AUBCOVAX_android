package com.eece451.aubcovax.api.models

class MedicalPersonnelModel {

    var id : Int ? = null
    var name : String ? = null
    var phoneNumber : String ? = null

    constructor(id : Int?, name : String?, phoneNumber : String?) {
        this.id = id
        this.name = name
        this.phoneNumber = phoneNumber
    }
}