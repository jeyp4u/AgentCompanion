package com.aanda.agent.companion.views

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.aanda.agent.companion.AppConstants
import com.aanda.agent.companion.BootReceiver
import com.aanda.agent.companion.DateUtils
import com.aanda.agent.companion.R
import com.aanda.agent.companion.network.AandAInterface
import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.network.NetworkManager
import com.aanda.agent.companion.views.data.Result
import com.aanda.agent.companion.views.data.model.Appointment
import com.aanda.agent.companion.views.data.model.LoggedInUser
import com.aanda.agent.companion.views.ui.dashBoard.AppointmentAdaptor
import com.aanda.agent.companion.views.ui.dashBoard.CircleTransform
import com.aanda.agent.companion.views.ui.dashBoard.DashBoardActivityViewModel
import com.aanda.agent.companion.views.ui.dashBoard.DashBoardViewModelFactory
import com.hypertrack.sdk.HyperTrack
import com.hypertrack.sdk.TrackingError
import com.hypertrack.sdk.TrackingStateObserver
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dash_board_actiivity_activity.*
import java.util.*

class DashBoardActivity : FragmentActivity() {


    private lateinit var adapter: AppointmentAdaptor
    private lateinit var models: List<Appointment>
    private lateinit var viewPager: ViewPager
    var sliderDotspanel: LinearLayout? = null
    private var dotscount = 0
    lateinit var progressCircle: ProgressBar
    private lateinit var dashBoardActivityViewModel: DashBoardActivityViewModel

    lateinit var user: LoggedInUser
    lateinit var message: TextView
    lateinit var hyperTrack: HyperTrack

    lateinit var sigout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dash_board_actiivity_activity)
        val user_name: TextView = findViewById(R.id.user_name)
        val user_picture: ImageView = findViewById(R.id.user_icon)

        try {
            user = intent!!.getParcelableExtra("user")
            user_name.text = getString(R.string.welcome, user!!.name)
            Picasso.get().load(user!!.image).transform(CircleTransform()).into(user_picture)


            sigout = findViewById(R.id.signout)

            sigout.setOnClickListener {
                logout()
            }
            message = findViewById(R.id.message)




            viewPager = findViewById(R.id.view_pager)
            sliderDotspanel = findViewById(R.id.slider_dots)
            progressCircle = findViewById(R.id.progress_circular)
            models = listOf()

            dashBoardActivityViewModel = ViewModelProviders.of(this, DashBoardViewModelFactory(ApiHelper(NetworkManager.buildService(AandAInterface::class.java))))
                    .get(DashBoardActivityViewModel::class.java)

            getAppointments()
            hyperTrack = HyperTrack.getInstance(this, AppConstants.HYPER_TRACK_ACCOUNT_PUBLISHABLE_KEY)


            hyperTrack.addTrackingListener(object : TrackingStateObserver.OnTrackingStateChangeListener {
                override fun onTrackingStart() {

                    sharing.setImageResource(R.drawable.ic_location_on_24px)
                    sharing.setColorFilter(ContextCompat.getColor(this@DashBoardActivity, android.R.color.holo_green_light), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                override fun onError(p0: TrackingError?) {
                    sharing.setImageResource(R.drawable.ic_location_off_24px)
                    sharing.setColorFilter(ContextCompat.getColor(this@DashBoardActivity, android.R.color.holo_red_light), android.graphics.PorterDuff.Mode.SRC_IN);
                }

                override fun onTrackingStop() {
                    sharing.setImageResource(R.drawable.ic_location_off_24px)
                    sharing.setColorFilter(ContextCompat.getColor(this@DashBoardActivity, android.R.color.holo_red_light), android.graphics.PorterDuff.Mode.SRC_IN);
                }

            })
            val sharing: ImageView = findViewById(R.id.sharing)


            if (hyperTrack.isRunning) {

                sharing.setImageResource(R.drawable.ic_location_on_24px)
                sharing.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light), android.graphics.PorterDuff.Mode.SRC_IN);
            } else {

                sharing.setImageResource(R.drawable.ic_location_off_24px)
                sharing.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light), android.graphics.PorterDuff.Mode.SRC_IN);


            }


        } catch (e: Exception) {
            Crashes.trackError(e);

        }


    }


    fun setIndicators() {

        try {


            dotscount = adapter.count
            message.text = getString(R.string.messge, "" + dotscount)

            val dots = arrayOfNulls<ImageView>(dotscount)

            for (i in 0 until dotscount) {
                dots[i] = ImageView(this)
                dots[i]!!.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(8, 0, 8, 0)
                sliderDotspanel!!.addView(dots[i], params)
            }
            dots[0]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot))

            viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    for (i in 0 until dotscount) {
                        dots[i]?.setImageDrawable(ContextCompat.getDrawable(this@DashBoardActivity, R.drawable.non_active_dot))
                    }
                    dots[position]?.setImageDrawable(ContextCompat.getDrawable(this@DashBoardActivity, R.drawable.active_dot))
                }
            })

        } catch (e: java.lang.Exception) {

            Crashes.trackError(e)

        }

    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun getAppointments() {

        try {


            Log.d(javaClass.name, "user id::" + user!!.id)
            Log.d(javaClass.name, "date::" + DateUtils.getCurrentDate())
            //track.isEnabled = false
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
                            models = resource.data.filterIsInstance<Appointment>()
                            Log.d(javaClass.name, "a" + models)
                        }
                        progressCircle.visibility = View.GONE
                        adapter = AppointmentAdaptor(models, this@DashBoardActivity)
                        viewPager.adapter = adapter

                        runOnUiThread(object : Runnable {
                            override fun run() {
                                setIndicators()
                            }

                        })
                        sheduleAppointments()
                    }
                }

            })
        } catch (e: java.lang.Exception) {

            Crashes.trackError(e)

        }
    }

    fun logout() {

        try {
            progressCircle.visibility = View.VISIBLE
            dashBoardActivityViewModel.logout(user!!.id).observe(this, Observer { resource ->
                resource ?: return@Observer
                when (resource) {
                    is Result.Error -> {
                        Log.d(javaClass.name, "Exception::" + resource.exception)
                        progressCircle.visibility = View.GONE

                    }
                    is Result.Success -> {

                        Log.d(javaClass.name, "Appoinments" + resource.data)

                        Log.d(javaClass.name, "appointments" + models)

                        progressCircle.visibility = View.GONE
                        Toast.makeText(applicationContext, "You have successfully logged out!...", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

            })
        } catch (e: java.lang.Exception) {

            Crashes.trackError(e)

        }
    }


    fun sheduleAppointments() {

        try {
            var count: Int = 0

            for (appoinment in models) {
                Log.d(javaClass.name, "appoinment.id on  " + appoinment.appointment_id)

                var time = appoinment.appointment_datetime

                Log.d(javaClass.name, "sheduleAppointments on  " + time)

                val actualCalender = DateUtils.getTriggerTime(time)
                val calendar = Calendar.getInstance()
                //calendar.timeInMillis = actualCalender.timeInMillis
                calendar[Calendar.YEAR] = actualCalender.get(Calendar.YEAR)
                calendar[Calendar.MONTH] = actualCalender.get(Calendar.MONTH)
                calendar[Calendar.DAY_OF_MONTH] = actualCalender.get(Calendar.DAY_OF_MONTH)
                calendar[Calendar.HOUR_OF_DAY] = actualCalender.get(Calendar.HOUR_OF_DAY)
                calendar[Calendar.MINUTE] = actualCalender.get(Calendar.MINUTE) - 30


                Log.d(javaClass.name, "sheduleAppointments   " + Date(calendar.timeInMillis))

                val intent = Intent(this, BootReceiver::class.java)
                intent.putExtra("negotiator_id", appoinment.negotiator_id)
                intent.putExtra("appointment_id", appoinment.appointment_id)
                // var pendingIntent = PendingIntent.getService(context, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager?
                var pendingIntent = PendingIntent.getBroadcast(this, count, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager!!.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                count++
                Log.d(javaClass.name, "sheduleAppointments count  " + count)
            }


        } catch (e: java.lang.Exception) {

            Crashes.trackError(e)

        }
    }

}
