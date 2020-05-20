package com.aanda.agent.companion.network

import com.aanda.agent.companion.views.data.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AandAInterface {

    @Headers(
            "Accept: application/json"
    )
    @POST(".")
    suspend fun login(@Body body: LoginRequest): String


    @GET("users")
    suspend fun getUsers(): List<User>

    @Headers(
            "Accept: application/json")
    @POST(".")
    suspend fun getAppoinmnets(@Body body: AppointmentRequest): Map<String?, Appointment?>?

    @POST(".")
    suspend fun sendAppointment(@Body body: SendAppointment): String

    @POST(".")
    suspend fun sendAppointmentTrackingStatus(@Body body: SendAppointmentTrackingStatus): String

    @POST(".")
    suspend fun logout(@Body body: LogoutRequest): LogoutResponse




    @POST(".")
    fun sendAppointmentAsync(@Body body: SendAppointment): Call<String>

    @POST(".")
    fun sendAppointmentTrackingStatusAsync(@Body body: SendAppointmentTrackingStatus): Call<String>


    @POST(".")
    fun loginAsync(@Body body: LoginRequest): Call<String>

    @POST(".")
    fun logoutAsync(@Body body: LogoutRequest): Call<LogoutResponse>

    @POST(".")
    fun getAppoinmnetsAsync(@Body body: AppointmentRequest): Call<Map<String?, Appointment?>?>

}