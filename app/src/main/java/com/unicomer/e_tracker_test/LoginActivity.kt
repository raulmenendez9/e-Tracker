package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.firebase.ui.auth.AuthUI

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentListener {

    // Inicializando componentes de Firebase / Auth
    lateinit var provider: List<AuthUI.IdpConfig>
    var splash: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Init
        provider = listOf(AuthUI.IdpConfig.EmailBuilder().build())
        loadLogin(LoginFragment())
        hideSplash()
    }

    fun hideSplash(){
        splash = findViewById(R.id.splashlogin)
        Handler().postDelayed({
            splash?.visibility = View.GONE
        },1000)
    }


    private fun loadLogin(login: LoginFragment){
        val transaction = supportFragmentManager
            .beginTransaction()
            .add(R.id.login_activity_fragment_container, login)
        transaction.commit()

    }



    override fun callMainFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
