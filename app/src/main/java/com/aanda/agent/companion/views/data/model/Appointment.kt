package com.aanda.agent.companion.views.data.model

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

data class Appointment(val appointment_id: String, val negotiator_id: String, val owner_type: String,
                       val appointment_datetime: String,
                       val duration: String,
                       val confirmed: String,
                       val cancelled: String,
                       val appointment_type: String,
                       val user_id: String,
                       val user_email: String,
                       val user_phone: String,
                       val user_name: String,
                       val appointment_created_at: String,
                       val property_id: String,
                       val latitude: String,
                       val longitude: String,
                       val price: String,
                       val rent_price: String,
                       val sale_price: String,
                       val currency_symbol: String,
                       val notification_status: String,
                       val travel_duration: String,
                       val property_type: String,
                       val address: String,
                       val status: String,
                       val internel_status: String,
                       val internal_sale_status: String,
                       val internal_letting_status: String,
                       val tracking: String,
                       val propertyimage_uri: String) {


}