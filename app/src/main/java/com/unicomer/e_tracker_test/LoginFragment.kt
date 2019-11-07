package com.unicomer.e_tracker_test

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.unicomer.e_tracker_test.dialogs.LoginDialogFragment


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LoginFragment : Fragment() {
    var signInButton: Button? = null
    var signInDialog: TextView?=null
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        signInButton = view?.findViewById(R.id.button_sign_in)
        signInDialog = view?.findViewById(R.id.txt_signin_problem)

        signInButton?.setOnClickListener {
            Toast.makeText(view?.context, "Needs authentication!", Toast.LENGTH_SHORT).show()
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
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

    }

    companion object {

        @JvmStatic
        fun newInstance() = LoginFragment()

    }
}
