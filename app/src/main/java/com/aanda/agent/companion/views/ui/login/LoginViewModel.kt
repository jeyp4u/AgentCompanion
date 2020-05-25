package com.aanda.agent.companion.views.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.annotation.UiThread
import androidx.lifecycle.liveData
import com.aanda.agent.companion.R
import com.aanda.agent.companion.views.data.LoginRepository
import com.aanda.agent.companion.views.data.Result
import kotlinx.coroutines.Dispatchers


class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult


//    fun login(username: String, password: String) {
//        // can be launched in a separate asynchronous job
//
//
//        val result = loginRepository.login(username, password)
//
//        if (result is Result.Success) {
//            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.name))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
//    }
//
//    fun getUsers() = liveData(Dispatchers.IO) {
//        emit(Result.Loading(data = "logging in..."))
//        try {
//            emit(Result.Success(data = loginRepository.getUsers()))
//        } catch (exception: Exception) {
//            emit(Result.Error(exception))
//        }
//    }


    fun login(username: String, password: String) = liveData(Dispatchers.IO) {
        Log.d("login", "Result.log in..")

        emit(Result.Loading(data = "logging in..."))

        try {
            emit(Result.Success(data = loginRepository.login(username, password)))

            Log.d("login", "Result.Success")
        } catch (exception: Exception) {
            emit(Result.Error(exception))
            Log.d("login", "Result.Exception" + exception)


        }
    }




//    fun loginAsync(username: String, password: String){
//
//        loginRepository.loginAsync(username,password)
//    }


    fun loginDataChanged(username: String, password: String) {


        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
