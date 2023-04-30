package com.eece451.aubcovax.api

import com.eece451.aubcovax.api.dtos.MedicalPersonnelDoseDto
import com.eece451.aubcovax.api.dtos.ResponseMessageDto
import com.eece451.aubcovax.api.models.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object AUBCOVAXService {

    private const val API_URL: String = "https://aubcovax.azurewebsites.net"

    fun AUBCOVAXApi() : AUBCOVAX {

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AUBCOVAX::class.java);
    }

    interface AUBCOVAX {

        @GET("/user")
        fun getUserInfo(@Header("Authorization") authorization: String?) : Call<PatientModel>

        @POST("/user")
        fun createPatientAccount(@Body patient : PatientModel) : Call<PatientModel>

        @POST("/authentication")
        fun login(@Body user : UserModel) : Call<LoginResponseModel>

        @GET("/user/patient")
        fun getAllPatients(@Header("Authorization") authorization: String?) : Call<List<PatientModel>>

        @GET("/user/personel")
        fun getAllMedicalPersonnel(@Header("Authorization") authorization: String?) : Call<List<MedicalPersonnelModel>>

        @GET("/user/reservation")
        fun getMyDoses(@Header("Authorization") authorization: String?) : Call<List<DoseModel>>

        @GET("/user/certificate")
        @Streaming
        fun getMyCertificate(@Header("Authorization") authorization: String?) : Call<ResponseBody>

        @GET("personel/patienthistory")
        fun getPatientDoses(@Header("Authorization") authorization: String?,
                            @Query("user") user: String?) : Call<List<DoseModel>>

        @POST("user/reserve")
        fun reserveFirstAppointment(@Header("Authorization") authorization: String?) : Call<Any>

        @POST("personel/reserve")
        fun reserveSecondAppointment(@Header("Authorization") authorization: String?,
                                     @Body medicalPersonnelDoseDto : MedicalPersonnelDoseDto
                                     ) : Call<ResponseMessageDto>

        @POST("personel/dose_confirm")
        fun confirmDose(@Header("Authorization") authorization: String?,
                        @Body medicalPersonnelDoseDto : MedicalPersonnelDoseDto
        ) : Call<Any>

        @GET("personel/reservation")
        fun getPersonnelAssignedDoses(@Header("Authorization") authorization: String?) : Call<List<DoseModel>>
    }
}