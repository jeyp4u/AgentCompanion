package com.aanda.agent.companion.views.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.aanda.agent.companion.R
import com.aanda.agent.companion.network.AandAInterface
import com.aanda.agent.companion.network.ApiHelper
import com.aanda.agent.companion.network.NetworkManager
import com.aanda.agent.companion.views.DashBoardActivity
import com.aanda.agent.companion.views.data.Result
import com.aanda.agent.companion.views.data.model.Appointment
import com.aanda.agent.companion.views.data.model.LoggedInUser
import com.aanda.agent.companion.views.data.model.LoginResponse
import com.aanda.agent.companion.views.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.json.JSONObject
import java.io.InputStream
import java.lang.reflect.Type


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        AppCenter.start(application, "2d1c0c20-8a7c-4d25-8fdd-504cfa94c7bb",
                Analytics::class.java, Crashes::class.java)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        login.isEnabled = true
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory(ApiHelper(NetworkManager.buildService(AandAInterface::class.java))))
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            //login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString()).observe(
                        this@LoginActivity,
                        Observer {

                            result ->
                            result ?: return@Observer



                            when (result) {
                                is Result.Error -> {
                                    showLoginFailed(""+result.exception)
                                    loading.visibility = View.GONE
                                    Crashes.trackError(result.exception)
                                }
                                is Result.Success -> {
                                    //updateUiWithUser(result.data)
                                    loading.visibility = View.GONE
                                    //Log.d("login", "result.data==" + result.data)

                                    val startindex = result.data.indexOf("{")

                                    var jsonObject: String = result.data.subSequence(startindex, result.data.length).toString()
                                    Log.d("login", "jsonObject==" + jsonObject)

                                    val result = HashMap<String, String>()
                                    result["LOGIN"] = jsonObject
                                    Analytics.trackEvent("Login", result);


                                    //  jsonObject = jsonObject.substring(0, jsonObject.indexOf("}"))
                                    val gson = Gson()

                                    if (jsonObject.contains("error")) {

                                        val obj: LoginResponse = gson.fromJson(jsonObject, LoginResponse::class.java)

                                        val errorcode = obj!!.error

                                        Toast.makeText(this@LoginActivity, "" + errorcode, Toast.LENGTH_SHORT).show()
                                        return@Observer
                                    }


                                    val obj: LoggedInUser = gson.fromJson(jsonObject, LoggedInUser::class.java)

                                    if (obj.id == null) {

                                        showLoginFailed("not valid response from server...")
                                    } else {
//
                                        Log.d("login", "LoggedInUser==" + obj)

                                        val dashboard: Intent = Intent(this@LoginActivity, DashBoardActivity::class.java)

                                        val result = HashMap<String, String>()
                                        result["USER"] = obj.id
                                        Analytics.trackEvent("Login success", result);
                                        dashboard.putExtra("user", obj)
                                        startActivity(dashboard)
                                        finish()
                                    }
                                    //getAppoinments()
                                }

                            }
                        }

                )
            }


            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this@LoginActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@LoginActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this@LoginActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            0)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }

        }
    }


    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = assets.open("apponments.json")
            json = inputStream.bufferedReader().use { it.readText() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome)
        val displayName = model.name
        // TODO : initiate successful logged in experience
        Toast.makeText(
                applicationContext,
                "$welcome $displayName",
                Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(value: String) {
        Toast.makeText(applicationContext, getString(R.string.login_failed) + " - " + value, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
