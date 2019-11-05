package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class LoginActivity : AppCompatActivity(), LoginFragment.OnFragmentInteractionListener {


    // Declarando Auth Firebase
    // [START declare_auth]
    var auth: FirebaseAuth? = null
    // [END declare_auth]


    private var customToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Instancia de Firebase Authentication
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()

        // Firebase Auth
        val currentUser = auth?.currentUser
    }

    private fun validarSessionDeUsuario(user: FirebaseUser) {
        if (user != null){
            // Este codigo pasa cuando el usuario SI esta autenticado
        } else {
            // Y este codigo se ejecuta cuando el usuario NO esta autenticado
        }
    }

    private fun setCustomToken(token: String) {
        customToken = token
    }

    private fun startSignIn() {
        // Iniciar Sign in with Custom Token
        // [START sign_in_custom]


    }
}
