package com.eece451.aubcovax.patient

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.eece451.aubcovax.ProgressBarManager
import com.eece451.aubcovax.R
import com.eece451.aubcovax.api.AUBCOVAXService
import com.eece451.aubcovax.api.Authentication
import com.eece451.aubcovax.api.Authentication.logout
import com.eece451.aubcovax.api.dtos.ResponseMessageDto
import com.eece451.aubcovax.api.models.PatientModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class PatientMeFragment : Fragment() {

    private val progressBarManager = ProgressBarManager()

    private var fullNameTextView : TextView? = null
    private var cardNumberTextView : TextView? = null
    private var phoneNumberTextView : TextView? = null
    private var emailTextView : TextView? = null
    private var dateOfBirthTextView : TextView? = null
    private var cityAndCountryTextView : TextView? = null
    private var medicalConditionsTextView : TextView? = null
    private var downloadCertificateButton : Button? = null
    private var logoutButton : Button? = null

    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "download_channel"
    private val CHANNEL_NAME = "Download"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPatientInfo()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.patient_me_fragment, container, false)

        fullNameTextView = view.findViewById(R.id.fullNameTextView)
        cardNumberTextView = view.findViewById(R.id.cardNumberTextView)
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        dateOfBirthTextView = view.findViewById(R.id.dateOfBirthTextView)
        cityAndCountryTextView = view.findViewById(R.id.cityAndCountryTextView)
        medicalConditionsTextView = view.findViewById(R.id.medicalConditionsTextView)

        downloadCertificateButton = view.findViewById(R.id.downloadCertificateButton)
        downloadCertificateButton?.setOnClickListener { _ -> downloadCertificate() }

        logoutButton = view.findViewById(R.id.logoutButton)
        logoutButton?.setOnClickListener { _ -> logout(requireContext()) }

        return view
    }

    @SuppressLint("MissingPermission")
    private fun downloadCertificate() {
        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            logout(requireContext())
        }

        createNotificationChannel(requireContext())

        AUBCOVAXService.AUBCOVAXApi().getMyCertificate("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    progressBarManager.hideProgressBar()

                    if(response.code() == 401 || response.code() == 403) {
                        logout(context)
                    }

                    if (response.isSuccessful) {

                        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.aubcovax_logo)
                            .setContentTitle("Downloading Certificate")
                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .setProgress(0, 0, true)
                            .setOngoing(true)

                        val notificationManager = NotificationManagerCompat.from(requireContext())
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

                        lifecycleScope.launch {
                            saveFile(response.body()!!, notificationBuilder, notificationManager)
                        }
                    }
                    else {
                        val errorResponse = Gson().fromJson(response.errorBody()!!.charStream(), ResponseMessageDto::class.java)
                        errorResponse.message?.let {
                            Snackbar.make(
                                fullNameTextView as View,
                                it,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        fullNameTextView as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }

    @SuppressLint("MissingPermission", "UnspecifiedImmutableFlag")
    private suspend fun saveFile(responseBody: ResponseBody,
                                 notificationBuilder: NotificationCompat.Builder,
                                 notificationManager: NotificationManagerCompat)  {
        withContext(Dispatchers.IO) {
            val downloadsDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, "certificate.pdf")

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val buffer = ByteArray(4096)
                responseBody.contentLength()
                var totalBytesRead: Long = 0

                inputStream = responseBody.byteStream()
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(buffer, 0, read)
                    totalBytesRead += read
                }

                outputStream.flush()

                val fileUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
                val openFileIntent = Intent(Intent.ACTION_VIEW)
                openFileIntent.setDataAndType(fileUri, "application/pdf")
                openFileIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION

                notificationBuilder
                    .setContentTitle("Download Complete")
                    .setContentIntent(
                        PendingIntent.getActivity(
                            requireContext(),
                            0,
                            openFileIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                        )
                    )
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

            }
            catch (_: IOException) { }
            finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun getPatientInfo() {

        progressBarManager.showProgressBar(requireActivity())

        if (Authentication.getToken() == null) {
            logout(requireContext())
        }
        AUBCOVAXService.AUBCOVAXApi().getUserInfo("Bearer ${Authentication.getToken()}")
            .enqueue(object : Callback<PatientModel> {

                override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                    progressBarManager.hideProgressBar()
                    if(response.isSuccessful) {
                        fullNameTextView?.text = "${response.body()?.firstName} ${response.body()?.lastName}"
                        cardNumberTextView?.text = response.body()?.idCardNumber
                        phoneNumberTextView?.text = response.body()?.phoneNumber
                        emailTextView?.text = response.body()?.email
                        dateOfBirthTextView?.text = response.body()?.dateOfBirth
                        cityAndCountryTextView?.text = "${response.body()?.city} - ${response.body()?.country}"
                        medicalConditionsTextView?.text = response.body()?.medicalConditions
                    }
                    else {
                        Snackbar.make(
                            logoutButton as View,
                            response.code().toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        if(response.code() == 401 || response.code() == 403) {
                            logout(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                    progressBarManager.hideProgressBar()
                    Snackbar.make(
                        logoutButton as View,
                        t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }

            })
    }
}