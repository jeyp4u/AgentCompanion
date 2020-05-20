package com.aanda.agent.companion.views.data.model

data class SendAppointment(
        val negotiator_id: String,
        val command: String,
        val token: String,
        val appointmentId: String) {
}