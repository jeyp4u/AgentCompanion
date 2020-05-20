package com.aanda.agent.companion.views.ui.dashBoard

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter
import com.aanda.agent.companion.DateUtils
import com.aanda.agent.companion.R
import com.aanda.agent.companion.views.data.model.Appointment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import java.text.SimpleDateFormat
import java.util.*


class AppointmentAdaptor(models: List<Appointment>, context: Context) : PagerAdapter() {
    private val models: List<Appointment>
    lateinit var layoutInflater: LayoutInflater
    private val context: Context
    override fun getCount(): Int {
        return models.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    lateinit var sheduledtime: TextView
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.view_appointment, container, false)
        val propertyImage: ImageView = view.findViewById(R.id.property_src)
        val propertyAddress: TextView = view.findViewById(R.id.property_address)

        val buyerName: TextView = view.findViewById(R.id.buyer_id)
        val buyeEmail: TextView = view.findViewById(R.id.buyer_email)

        val appointmentTime: TextView = view.findViewById(R.id.appoinment_time)
        val appoinmnet_status: TextView = view.findViewById(R.id.appoinmnet_status)
        val phoneNumber: TextView = view.findViewById(R.id.buyer_phone)
        sheduledtime = view.findViewById(R.id.sheduledtime)

        propertyAddress.setText(models[position].address)
        phoneNumber.setText(models[position].user_phone)
        buyerName.setText(models[position].user_name)
        buyeEmail.setText(models[position].user_email)

        val appointmentDate: TextView = view.findViewById(R.id.appoinmnet_date)
        appointmentDate.setText(DateUtils.getAppointmentDate(models[position].appointment_datetime))
        appointmentTime.setText(DateUtils.getAppointmentTime(models[position].appointment_datetime))
        appoinmnet_status.setText(models[position].appointment_type)

        val sheduleTIME: String = DateUtils.getBeforeSheduledTIme(DateUtils.getAppointmentDateValue(models[position].appointment_datetime))


        sheduledtime.setText(context.getString(R.string.scheduled_at,sheduleTIME ))


        propertyAddress.setOnClickListener {

            navigateToGMap(models[position].latitude, models[position].longitude)
        }
        Glide.with(context)
                .asBitmap()
                .load(models[position].propertyimage_uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                        propertyImage.setImageBitmap(resource)
                    }
                })



        container.addView(view, 0)
        return view
    }

    fun navigateToGMap(lat: String, lon: String) {

        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=:" + lat + "," + lon)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    init {
        this.models = models
        this.context = context
    }
}