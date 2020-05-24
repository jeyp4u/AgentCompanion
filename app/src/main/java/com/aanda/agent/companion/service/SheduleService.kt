package com.aanda.agent.companion.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.aanda.agent.companion.R
import com.aanda.agent.companion.network.AandAInterface
import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.network.NetworkManager
import com.aanda.agent.companion.views.DashBoardActivity
import com.aanda.agent.companion.views.data.model.SendAppointment
import com.aanda.agent.companion.views.data.model.SendAppointmentTrackingStatus
import com.aanda.agent.companion.views.ui.login.LoginActivity
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingError
import com.hypertrack.sdk.TrackingStateObserver
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class SheduleService : IntentService("SheuledService") {
    lateinit var hyperTrack: HyperTrack
    lateinit var apiHelper: ApiHelper
    private val PUBLISHABLE_KEY = "7sZoVue6BIpk_aVJjDzkz2BdU2RcQ1NF4RPcCT2kk_Jmmajpg1HedMwZNrKy8tiTyJb0EGeEg7jLPR04WwfGnQ"

    lateinit var negotiator_id: String
    lateinit var appointment_id: String

    override fun onHandleIntent(intent: Intent?) {

        negotiator_id = intent!!.getStringExtra("negotiator_id")
        appointment_id = intent!!.getStringExtra("appointment_id")

        Log.d(javaClass.name, "onHandleIntent appointment_id" + appointment_id)
        //createNotificationChannel()

        if (!AppCenter.isConfigured()) {
            AppCenter.start(application, "2d1c0c20-8a7c-4d25-8fdd-504cfa94c7bb",
                    Analytics::class.java, Crashes::class.java)
        }
        startTracking()

    }

    override fun onCreate() {
        super.onCreate()
        hyperTrack = HyperTrack.getInstance(this.applicationContext, PUBLISHABLE_KEY)
        apiHelper = ApiHelper(NetworkManager.buildService(AandAInterface::class.java))
        startForeground(1, Notification())

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = "Location sharing initiated..."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("9999", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startAgentTrackingAsync() {

        try {

            apiHelper.sendAppointmentAsync(SendAppointment(negotiator_id,
                    "post_appointment_notification",
                    "ZegwMHyhYo3zEujwm9yF",
                    appointment_id)).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(javaClass.name, "appoinmentResult onFailure " + t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d(javaClass.name, "appoinmentResult onResponse " + response)
                }
            })






            apiHelper.sendAppointmentTrackingStatusAsync(SendAppointmentTrackingStatus(negotiator_id = negotiator_id,
                    command = "post_appointment_tracking_status",
                    token = "ZegwMHyhYo3zEujwm9yF",
                    appointmentId = appointment_id, status = "1")).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(javaClass.name, "sendAppointmentTrackingStatusAsync onFailure " + t)

                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d(javaClass.name, "sendAppointmentTrackingStatusAsync onResponse " + response + call.request().body)
                }
            })
            // Toast.makeText(applicationContext, "Location sharing initiated...", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Crashes.trackError(e)
        }
    }


//    fun startAgentTracking() {
//
//
//        val ioScope = CoroutineScope(Dispatchers.IO + Job())
//        ioScope.launch {
//            val appoinmentResult = apiHelper.sendAppointment(SendAppointment(negotiator_id,
//                    "post_appointment_notification",
//                    "ZegwMHyhYo3zEujwm9yF",
//                    appointment_id))
//
//            Log.d(javaClass.name, "appoinmentResult " + appoinmentResult)
//
//            val trackingstatus = apiHelper.sendAppointmentTrackingStatus(SendAppointmentTrackingStatus(negotiator_id = negotiator_id,
//                    command = "post_appointment_tracking_status",
//                    token = "ZegwMHyhYo3zEujwm9yF",
//                    appointmentId = appointment_id, status = "1"))
//
//            Log.d(javaClass.name, "trackingstatus" + trackingstatus)
//
//        }
//    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun startTracking() {

        if (hyperTrack.isRunning) {
            Log.d(javaClass.name, "hyperTrack already running ")
            startAgentTrackingAsync()
            return
        }
        hyperTrack.start().addTrackingListener(object : TrackingStateObserver.OnTrackingStateChangeListener {
            override fun onTrackingStart() {

                Log.d(javaClass.name, "hyperTrack onTrackingStart")
                startAgentTrackingAsync()
            }

            override fun onError(p0: TrackingError?) {
                Log.d(javaClass.name, "hyperTrack TrackingError" + p0)
                Toast.makeText(applicationContext, "Hypertrack tracking error", Toast.LENGTH_LONG).show()
            }

            override fun onTrackingStop() {
                Log.d(javaClass.name, "hyperTrack onTrackingStop")
                Toast.makeText(applicationContext, "Hypertrack tracking stopped", Toast.LENGTH_LONG).show()


            }
        })
    }

    override fun onBind(arg0: Intent?): IBinder? {
//        Log.d(javaClass.name, "Service got created")
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        //Toast.makeText(this, "ServiceClass.onStart()", Toast.LENGTH_LONG).show()
//        Log.d(javaClass.name, "Service got started")
    }
}