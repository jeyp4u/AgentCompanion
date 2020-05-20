package com.aanda.agent.companion.views.ui.dashBoard

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aanda.agent.companion.AppConstants.HYPER_TRACK_ACCOUNT_PUBLISHABLE_KEY
import com.aanda.agent.companion.BootReceiver
import com.aanda.agent.companion.DateUtils
import com.aanda.agent.companion.R
import com.aanda.agent.companion.network.AandAInterface
import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.network.NetworkManager
import com.aanda.agent.companion.service.SheduleService
import com.aanda.agent.companion.views.data.Result
import com.aanda.agent.companion.views.data.model.Appointment
import com.aanda.agent.companion.views.data.model.LoggedInUser
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingError
import com.hypertrack.sdk.TrackingStateObserver
import kotlinx.android.synthetic.main.dash_board_actiivity_fragment.view.*
import org.json.JSONObject
import java.io.InputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


class DashBoardActivityFragment : Fragment() {

    companion object {
        fun newInstance() = DashBoardActivityFragment()
    }

    lateinit var user: LoggedInUser
    private lateinit var dashBoardActivityViewModel: DashBoardActivityViewModel
    lateinit var appointmentsList: List<Appointment>
    lateinit var hyperTrack: HyperTrack



    lateinit var track: Button
    lateinit var messgae: TextView
    lateinit var sync: FloatingActionButton

    lateinit var progressCircle: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.dash_board_actiivity_fragment, container, false)
        track = view.findViewById(R.id.track)
        track.isEnabled = false
        track.visibility = View.GONE
        progressCircle = view.findViewById(R.id.progress_circular)
        view.track.setOnClickListener {
            startTracking()
        }
        messgae = view.findViewById(R.id.message)
        messgae.text = getString(R.string.status_not_track)

        sync = view.findViewById(R.id.getappointment)

        sync.setOnClickListener {
            getAppointments()

            //sheduleAppointments()
        }


        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dashBoardActivityViewModel = ViewModelProviders.of(this, DashBoardViewModelFactory(ApiHelper(NetworkManager.buildService(AandAInterface::class.java))))
                .get(DashBoardActivityViewModel::class.java)
        user = activity!!.intent.getParcelableExtra("user")

        hyperTrack = HyperTrack.getInstance(this.context, HYPER_TRACK_ACCOUNT_PUBLISHABLE_KEY)
        //startTracking()
    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun getAppointments() {
        Log.d(javaClass.name, "user id::" + user!!.id)
        Log.d(javaClass.name, "date::" + DateUtils.getCurrentDate())
        track.isEnabled = false
        progressCircle.visibility = View.VISIBLE
        dashBoardActivityViewModel.getAppointments(user!!.id, DateUtils.getCurrentDate()).observe(this, Observer { resource ->
            resource ?: return@Observer
            when (resource) {
                is Result.Error -> {
                    Log.d(javaClass.name, "Exception::" + resource.exception)

                    progressCircle.visibility = View.GONE

                }
                is Result.Success -> {

                    Log.d(javaClass.name, "Appoinments" + resource.data)

                    if (resource.data is Collection<*>) {
                        appointmentsList = resource.data.filterIsInstance<Appointment>()
                        Log.d(javaClass.name, "a" + appointmentsList)

                    }
                    Log.d(javaClass.name, "Appoinments" + appointmentsList)
                    progressCircle.visibility = View.GONE

                    track.isEnabled = true

                    sheduleAppointments()
                }
            }

        })
    }


    fun getTriggerTime(appoinmenttime: String): Calendar {

        val cal = Calendar.getInstance()
        // 2020-05-12 09:00:00
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        cal.time = sdf.parse(appoinmenttime) //

        Log.d(javaClass.name, "cal.time " + cal.time)

        return cal
    }

    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = activity!!.assets.open("apponments.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    fun sheduleAppointments() {


//        var obj = JSONObject(readJSONFromAsset())
//        val gson = Gson()
//        val mapType: Type = object : TypeToken<Map<String?, Appointment?>?>() {}.type
//        val logs = gson.fromJson<Map<String?, Appointment?>>(readJSONFromAsset(), mapType)

        var count: Int = 0

        for (appoinment in appointmentsList) {
            Log.d(javaClass.name, "appoinment.id on  " + appoinment.appointment_id)
//            if (!appoinment.appointment_id.equals("dubai-dub2011636")) {
//                continue
//            }
            var time = appoinment.appointment_datetime

            Log.d(javaClass.name, "sheduleAppointments on  " + time)

            val actualCalender = getTriggerTime(time)
            val calendar = Calendar.getInstance()
            //calendar.timeInMillis = actualCalender.timeInMillis
            calendar[Calendar.YEAR] = actualCalender.get(Calendar.YEAR)
            calendar[Calendar.MONTH] = actualCalender.get(Calendar.MONTH)
            calendar[Calendar.DAY_OF_MONTH] = actualCalender.get(Calendar.DAY_OF_MONTH)
            calendar[Calendar.HOUR_OF_DAY] = actualCalender.get(Calendar.HOUR_OF_DAY)
            calendar[Calendar.MINUTE] = actualCalender.get(Calendar.MINUTE)
            val intent = Intent(context, BootReceiver::class.java)
            intent.putExtra("negotiator_id", appoinment.negotiator_id)
            intent.putExtra("appointment_id", appoinment.appointment_id)
            // var pendingIntent = PendingIntent.getService(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            var pendingIntent = PendingIntent.getBroadcast(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)


            count++
            Log.d(javaClass.name, "sheduleAppointments count  " + calendar.get(Calendar.MINUTE))
        }

//        var count: Int = 0
//        for (appoinment in appointmentsList) {
//            var time = appoinment.appointment_datetime
//
//            Log.d(javaClass.name, "sheduleAppointments on  " + time)
//
//            val actualCalender = getTriggerTime(time)
//            val calendar = Calendar.getInstance()
//        calendar[Calendar.YEAR] = actualCalender.get(Calendar.YEAR)
//        calendar[Calendar.MONTH] = actualCalender.get(Calendar.MONTH)
//        calendar[Calendar.DAY_OF_MONTH] = actualCalender.get(Calendar.DAY_OF_MONTH)
//        calendar[Calendar.HOUR_OF_DAY] = actualCalender.get(Calendar.HOUR_OF_DAY)
//        calendar[Calendar.MINUTE] = actualCalender.get(Calendar.MINUTE)
//            val intent = Intent(context, SheduleService::class.java)
//            intent.putExtra("negotiator_id", appoinment.negotiator_id)
//            intent.putExtra("appointment_id", appoinment.appointment_id)
//            val pendingIntent = PendingIntent.getService(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
//            alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//            count++
//
//            Log.d(javaClass.name, "sheduleAppointments count  " + count)
//
//        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun startTracking() {

        if (hyperTrack.isRunning) {
            Log.d(javaClass.name, "hyperTrack already running ")

            return
        }
        hyperTrack.start().addTrackingListener(object : TrackingStateObserver.OnTrackingStateChangeListener {
            override fun onTrackingStart() {
                //startAppointmnet();

                Log.d(javaClass.name, "hyperTrack onTrackingStart")
                messgae.text = getString(R.string.status_track)

            }

            override fun onError(p0: TrackingError?) {
                Log.d(javaClass.name, "hyperTrack TrackingError" + p0)
                messgae.text = getString(R.string.status_track)

            }

            override fun onTrackingStop() {
                Log.d(javaClass.name, "hyperTrack onTrackingStop")
                messgae.text = getString(R.string.status_track)

            }
        })
    }


    fun startAppointmnet() {
        progressCircle.visibility = View.VISIBLE

        val appointment: Appointment = appointmentsList.get(1)

        Log.d(javaClass.name, "user id::" + appointment)
        dashBoardActivityViewModel.sendAppointmentNotification(appointment.negotiator_id, appointment.appointment_id).observe(this, Observer { resource ->
            resource ?: return@Observer
            when (resource) {
                is Result.Error -> {
                    Log.d(javaClass.name, "Exception::" + resource.exception)
                }
                is Result.Success -> {
                    Log.d(javaClass.name, "sendAppointmentNotification result" + resource.data)
                }
            }
        })


        dashBoardActivityViewModel.sendAppointmentTrackingStatus(appointmentId = appointment.appointment_id, negotiator_id = appointment.negotiator_id, status = "1").observe(this, Observer { resource ->
            resource ?: return@Observer
            when (resource) {
                is Result.Error -> {
                    Log.d(javaClass.name, "Exception::" + resource.exception)
                }
                is Result.Success -> {
                    Log.d(javaClass.name, "sendAppointmentTrackingStatus result" + resource.data)

                }
            }

        })

        progressCircle.visibility = View.GONE

    }
}
