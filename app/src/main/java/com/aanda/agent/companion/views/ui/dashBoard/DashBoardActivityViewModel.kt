package com.aanda.agent.companion.views.ui.dashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.aanda.agent.companion.views.data.Result
import kotlinx.coroutines.Dispatchers

class DashBoardActivityViewModel(val dashBoardRepository: DashBoardRepository) : ViewModel() {

    fun getAppointments(negotiator_id: String, date: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading(data = "logging in..."))
        try {
            emit(Result.Success(data = dashBoardRepository.getAppointments(negotiator_id, date)!!.values))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun logout(negotiator_id: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading(data = "logging out..."+negotiator_id))
        try {
            emit(Result.Success(data = dashBoardRepository.logout(negotiator_id)!!.success))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }


    fun sendAppointmentNotification(negotiator_id: String, appointmentId: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading(data = "logging in..."))
        try {
            emit(Result.Success(data = dashBoardRepository.sendAppointment(negotiator_id, appointmentId)))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

    fun sendAppointmentTrackingStatus(negotiator_id: String, appointmentId: String, status: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading(data = "logging in..."))
        try {
            emit(Result.Success(data = dashBoardRepository.sendAppointmentTrackingStatus(negotiator_id, appointmentId, status)))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }

}
