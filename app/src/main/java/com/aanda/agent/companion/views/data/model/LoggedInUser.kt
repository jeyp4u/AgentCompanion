package com.aanda.agent.companion.views.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Parcelize
data class LoggedInUser(
        val id: String,
        val name: String,val mobile: String,val email: String,val telephone: String,val jobtitle: String,val image_name: String,val office_id: String,val online_status: String?,val image: String,val custom_user_id: String,val tracking_url: String
):Parcelable
