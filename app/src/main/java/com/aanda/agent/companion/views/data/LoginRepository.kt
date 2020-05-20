package com.aanda.agent.companion.views.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.views.data.model.AppointmentRequest
import com.aanda.agent.companion.views.data.model.LoggedInUser
import com.aanda.agent.companion.views.data.model.LoginRequest
import com.aanda.agent.companion.views.ui.login.LoggedInUserView
import com.aanda.agent.companion.views.ui.login.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val apiHelper: ApiHelper) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        //dataSource.logout()
    }

    suspend fun login(username: String, password: String) = apiHelper.login(LoginRequest(username, password, "login", "ZegwMHyhYo3zEujwm9yF"))
//    suspend fun getUsers() = apiHelper.getUsers()


    //fun loginAsync(username: String, password: String) = apiHelper.loginAsync(LoginRequest(username, password, "login", "ZegwMHyhYo3zEujwm9yF"))
    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }


//    fun loginAsync(username: String, password: String): MutableLiveData<LoginResult> {
//
//        var results: MutableLiveData<LoginResult> = MutableLiveData()
//        apiHelper.loginAsync(LoginRequest(username, password, "login", "ZegwMHyhYo3zEujwm9yF")).enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                results.value = LoginResult(error = t.message)
//            }
//
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//              //  results.value = LoginResult(success = LoggedInUserView(displayName = response.))
//            }
//
//        })
//
//    }
}
