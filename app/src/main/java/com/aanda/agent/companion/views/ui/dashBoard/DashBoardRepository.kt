package com.aanda.agent.companion.views.ui.dashBoard

import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.views.data.model.AppointmentRequest
import com.aanda.agent.companion.views.data.model.LogoutRequest
import com.aanda.agent.companion.views.data.model.SendAppointment
import com.aanda.agent.companion.views.data.model.SendAppointmentTrackingStatus

class DashBoardRepository(val apiHelper: ApiHelper) {
    suspend fun getAppointments(negotiator_id: String, date: String) = apiHelper.getAppointments(AppointmentRequest(negotiator_id, "negotiator_appointment", "ZegwMHyhYo3zEujwm9yF", date))
    suspend fun sendAppointment(negotiator_id: String, appointmentId: String) = apiHelper.sendAppointment(SendAppointment(negotiator_id, "post_appointment_notification", "ZegwMHyhYo3zEujwm9yF", appointmentId))
    suspend fun sendAppointmentTrackingStatus(negotiator_id: String, appointmentId: String, status: String) = apiHelper.sendAppointmentTrackingStatus(SendAppointmentTrackingStatus("post_appointment_tracking_status", "ZegwMHyhYo3zEujwm9yF", negotiator_id, appointmentId, status = status))

    suspend fun logout(negotiator_id: String) = apiHelper.logout(LogoutRequest("logout", "ZegwMHyhYo3zEujwm9yF", negotiator_id))

}