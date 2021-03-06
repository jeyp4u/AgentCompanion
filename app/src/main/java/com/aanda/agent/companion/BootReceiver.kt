package com.aanda.agent.companion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.aanda.agent.companion.service.SheduleService
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import java.lang.Exception


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        try {
            //Toast.makeText(context!!.getApplicationContext(), "Alarm Manager ran....", Toast.LENGTH_LONG).show()
            val serviceIntent = Intent(context, SheduleService::class.java)
            serviceIntent.putExtra("negotiator_id", intent!!.getStringExtra("negotiator_id"))
            serviceIntent.putExtra("appointment_id", intent!!.getStringExtra("appointment_id"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context!!.startForegroundService(serviceIntent)
            } else {
                context!!.startService(serviceIntent)
            }
            if (intent!!.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            } else {

            }
        } catch (e: Exception) {
            Toast.makeText(context!!.getApplicationContext(), "Not able to Start Service....", Toast.LENGTH_LONG).show()
            Crashes.trackError(e)
        }
    }


}