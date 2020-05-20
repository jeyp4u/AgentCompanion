package com.aanda.agent.companion.views.data.model


data class LogoutRequest(val command: String, val token: String,val negotiator_id: String) {
}