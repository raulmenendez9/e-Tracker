package com.unicomer.e_tracker_test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.fragment.app.FragmentManager
import com.unicomer.e_tracker_test.dialogs.LoginDialogFragment

class LoginFragment : Fragment() {
    // FirebaseAuth instancia
    val dbAuth = FirebaseAuth.getInstance()

    // Declarando elementos del UI
    var emailText: TextView? = null
    var passwordText: TextView? = null
    var signInButton: Button? = null

    // Listener
    private var listener: LoginFragmentListener? = null

    var signInDialog: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // Validar si el usuario ya inicio sesion
        val user = dbAuth.currentUser
        // currentUser determina el usuario y con currentUser se agrega el fragment con sesion iniciada
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailText = view?.findViewById(R.id.et_email)
        passwordText = view?.findViewById(R.id.et_password)

        signInButton = view?.findViewById(R.id.button_sign_in)
        signInDialog = view?.findViewById(R.id.txt_signin_problem)

        signInButton?.setOnClickListener {
            loginIn()
        }
        signInDialog?.setOnClickListener {
            showDialog()
        }
        return view
    }

    fun showDialog(){
        val fm = activity!!.supportFragmentManager
        val dialog = LoginDialogFragment()
        dialog.show(fm,"DIALOG_LOGIN")

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement LoginFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun loginIn() {

        if (emailText?.text.toString().isEmpty() or passwordText?.text.toString().isEmpty()) {
            Toast.makeText(this.context, "Correo y contrasena son obligatorios", Toast.LENGTH_SHORT)
                .show()
        } else {
            dbAuth.signInWithEmailAndPassword(emailText?.text.toString(), passwordText?.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        // Si authentication funciona aqui se maneja
                        // Falta implementar los listeners

                        Log.i("LoginFragment", "signIn:success")
                        val user = dbAuth.currentUser
                        mostrarMainActivity(user!!)
                        Log.i("LoginFragment","Successfully logged in with UID ${it.result?.user?.uid}")
                    } else {

                        // Si authentication falla aqui puede manejarse

                        Log.w("", "signIn:failure", it.exception)
                        Toast.makeText(this.context,"Autenticacion fallida.", Toast.LENGTH_LONG).show()
                        // mostrarMainActivity(null)
                    }
                }

        }
    }

    private fun mostrarMainActivity(user: FirebaseUser){
        val intent = Intent(this.context, MainActivity::class.java)
        startActivity(intent)
    }
    interface LoginFragmentListener {
        fun callMainFragment()
    }
    companion object{
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}

