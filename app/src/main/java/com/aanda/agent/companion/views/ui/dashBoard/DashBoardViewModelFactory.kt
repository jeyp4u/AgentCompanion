package com.aanda.agent.companion.views.ui.dashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aanda.agent.companion.network.ApiHelper

class DashBoardViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashBoardActivityViewModel::class.java)) {
            return DashBoardActivityViewModel(
                    dashBoardRepository = DashBoardRepository(
                            apiHelper
                    )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}