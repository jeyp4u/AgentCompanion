package com.aanda.agent.companion.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.aanda.agent.companion.R
import com.aanda.agent.companion.views.ui.login.LoginActivity

class SplashScreen : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(object : Runnable {
            override fun run() {
                var mainIntent = Intent(this@SplashScreen, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        }, 3000L)
    }
}