package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentListener {


    // Inicializando componentes de Firebase / Auth
    lateinit var provider: List<AuthUI.IdpConfig>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init
        provider = listOf(AuthUI.IdpConfig.EmailBuilder().build())



        loadLogin(LoginFragment())

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
