package com.aanda.agent.companion.views.data.model

data class LoginRequest(val username: String, val password: String, val command: String, val token: String) {
}