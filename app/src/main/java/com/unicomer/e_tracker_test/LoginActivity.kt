package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

class LoginActivity : AppCompatActivity(), LoginFragment.OnFragmentInteractionListener {
    var splash: View? = null //contenedor del splash
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        showSplash()
    }


    fun showSplash(){
        splash = findViewById(R.id.splashlogin)
        Handler().postDelayed({
            splash?.visibility = View.GONE
        },1000)
    }
}
