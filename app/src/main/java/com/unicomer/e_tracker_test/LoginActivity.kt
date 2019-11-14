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
import com.unicomer.e_tracker_test.Constants.APP_NAME
import com.unicomer.e_tracker_test.Constants.LOGIN_ACTIVITY_KEY
import com.unicomer.e_tracker_test.Constants.USER_LOGGED_IN_KEY
import com.unicomer.e_tracker_test.dialogs.LoginDialogFragment

class LoginActivity : AppCompatActivity() {

    // Declaring FirebaseAuth components
    private var dbAuth: FirebaseAuth? = null


    // UI variables
    var emailText: TextView? = null
    var passwordText: TextView? = null
    var signInButton: Button? = null
    var signInDialog: TextView? = null



    // Splash for Login (no se muestra si user != null)

    var splash: View? = null

    // End of Splash declaration

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // Validar si el usuario ha logeado anteriormente

        dbAuth = FirebaseAuth.getInstance()
        val user = dbAuth!!.currentUser

        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        emailText = this.findViewById(R.id.et_email)
        passwordText = this.findViewById(R.id.et_password)

        signInButton = this.findViewById(R.id.button_sign_in)
        signInDialog = this.findViewById(R.id.txt_signin_problem)

        signInButton?.setOnClickListener {
            validateUserSession()
        }
        signInDialog?.setOnClickListener {
            showDialog()
        }



    }

    fun hideSplash(){
        splash = findViewById(R.id.splashlogin)
        Handler().postDelayed({
            splash?.visibility = View.GONE
        },1000)
    }



    private fun validateUserSession(){

        if (emailText?.text.toString().isEmpty() or passwordText?.text.toString().isEmpty()) {
            Toast.makeText(this, R.string.required_email_password, Toast.LENGTH_SHORT)
                .show()
        } else {
            dbAuth?.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())?.addOnCompleteListener {
                    if (it.isSuccessful) {

                        // Si authentication funciona aqui se maneja
                        // Falta implementar los listeners

                        Log.i(LOGIN_ACTIVITY_KEY, "signIn:success")
                        Log.i(LOGIN_ACTIVITY_KEY,"Successfully logged in with UID ${it.result?.user?.uid}")

                        val userLoggedIn = dbAuth?.currentUser


                        val sharedPreferences = this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putString(USER_LOGGED_IN_KEY, userLoggedIn!!.email)
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        // Si authentication falla aqui puede manejarse

                        Log.w("", "signIn:failure", it.exception)
                        Toast.makeText(this,R.string.wrong_email_password, Toast.LENGTH_LONG).show()
                    }
                }
        }

    }

    // TODO REVISAR ESTO

    fun showDialog(){
        val fm = this.supportFragmentManager
        val dialog = LoginDialogFragment()
        dialog.show(fm,"DIALOG_LOGIN")

    }

}
