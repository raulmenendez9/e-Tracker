package com.unicomer.e_tracker_test

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unicomer.e_tracker_test.Constants.*
import com.unicomer.e_tracker_test.Dialogs.LoginDialogFragment

class LoginActivity : AppCompatActivity() {

    // Declaration of FirebaseAuthLocalClass components
    private var dbAuth: FirebaseAuth? = null
    // End of declaration FirebaseAuthLocalClass components


    // Declaration of variables
    var emailText: TextView? = null
    var passwordText: TextView? = null
    var signInButton: Button? = null
    var signInDialog: TextView? = null
    // End of declaration UI variables


    // Splash for Login (no se muestra si user != null)
    var splash: View? = null
    // End of Splash declaration



    // Activity Lifecycle methods here



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.i(LOGIN_ACTIVITY_KEY, "In method onCreate")

        // Validar si el usuario ha logeado anteriormente

        dbAuth = FirebaseAuth.getInstance()
        val user = dbAuth!!.currentUser

        // Si usuario NO es null entonces LoginActivity NO se ejecuta y pasa directamente a MainActivity
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onStart")

        emailText = this.findViewById(R.id.et_email)
        passwordText = this.findViewById(R.id.et_password)
        signInButton = this.findViewById(R.id.button_sign_in)
        signInDialog = this.findViewById(R.id.txt_signin_problem)

    }

    override fun onResume() {
        super.onResume()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onResume")

        signInButton?.setOnClickListener {
            validateUserSession()
        }
        signInDialog?.setOnClickListener {
            showDialog()
        }
    }

    override fun onPause() {
        super.onPause()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onPause")

    }

    override fun onStop() {
        super.onStop()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onStop")

    }

    override fun onRestart() {
        super.onRestart()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(LOGIN_ACTIVITY_KEY, "In method onDestroy")
    }

    fun hideSplash(){
        splash = findViewById(R.id.splashlogin)
        Handler().postDelayed({
            splash?.visibility = View.GONE
        },1000)
    }



    private fun validateUserSession(){

        // Validar los campos Email & Password
        if (emailText?.text.toString().isEmpty() or passwordText?.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.required_email_password, Toast.LENGTH_SHORT)
                .show()
        } else {
            dbAuth?.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())?.addOnCompleteListener {
                    if (it.isSuccessful) {

                        // Si authentication funciona aqui se maneja

                        Log.i(LOGIN_ACTIVITY_KEY, "signIn:success")
                        Log.i(LOGIN_ACTIVITY_KEY,"Successfully logged in with user ${it.result?.user?.email} and UID ${it.result?.user?.uid}")

                        val userLoggedIn = dbAuth?.currentUser

                        // Guardar los datos en SharedPreferences (Informacion Local)

                        val sharedPreferences = this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putString(FIREBASE_CURRENT_USER_KEY, userLoggedIn.toString())
                        editor.putString(FIREBASE_USER_EMAIL_LOGGED_IN_KEY, userLoggedIn!!.email)
                        editor.putString(FIREBASE_USER_UID_KEY, userLoggedIn.uid)
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        // Si authentication falla aqui puede manejarse

                        Log.w(LOGIN_ACTIVITY_KEY, "signIn:failure", it.exception)
                        Toast.makeText(this,R.string.wrong_email_password, Toast.LENGTH_LONG).show()
                    }
                }
        }

    }



    // TODO REVISAR ESTO

    fun showDialog(){
        val fm = this.supportFragmentManager
        val dialog = LoginDialogFragment.newInstance()
        dialog.show(fm, LOGIN_DIALOG)

    }

}
