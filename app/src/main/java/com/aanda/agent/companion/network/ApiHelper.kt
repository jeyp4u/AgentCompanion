package com.aanda.agent.companion.network

import com.aanda.agent.companion.views.data.model.*
import retrofit2.Call
import retrofit2.http.Body

class ApiHelper(private val apiService: AandAInterface) {
    suspend fun login(@Body body: LoginRequest) = apiService.login(body)

    suspend fun getUsers() = apiService.getUsers()

    suspend fun getAppointments(@Body body: AppointmentRequest) = apiService.getAppoinmnets(body)


    suspend fun sendAppointment(@Body body: SendAppointment) = apiService.sendAppointment(body)

    suspend fun sendAppointmentTrackingStatus(@Body body: SendAppointmentTrackingStatus) = apiService.sendAppointmentTrackingStatus(body)

    suspend fun logout(@Body body: LogoutRequest) = apiService.logout(body)



    fun loginAsync(@Body body: LoginRequest) = apiService.loginAsync(body)
    fun logoutAsync(@Body body: LogoutRequest) = apiService.logoutAsync(body)
    fun getAppointmentsAsync(@Body body: AppointmentRequest) = apiService.getAppoinmnetsAsync(body)
    fun sendAppointmentAsync(@Body body: SendAppointment): Call<String> = apiService.sendAppointmentAsync(body)
    fun sendAppointmentTrackingStatusAsync(@Body body: SendAppointmentTrackingStatus): Call<String> = apiService.sendAppointmentTrackingStatusAsync(body)

}