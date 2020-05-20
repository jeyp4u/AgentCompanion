package com.aanda.agent.companion.views.data.model

data class SendAppointmentTrackingStatus(
        val command: String,
        val token: String,
        val negotiator_id: String,
        val appointmentId: String, val status: String
) {
}