package com.unicomer.e_tracker_test.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.classes.CreateEmail


class CreateReportDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    var btnCancel: Button? = null
    var btnSend:Button?=null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_report_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCancel = view.findViewById(R.id.button_cancel2)
        btnSend = view.findViewById(R.id.button_send2)
    }

    override fun onResume() {
        super.onResume()
        btnCancel!!.setOnClickListener {
            dialog!!.cancel()
        }
        btnSend!!.setOnClickListener {
            //var emailIntent=
                CreateEmail("eU4tRDVMD41ypsOU8Pzz").email()
            //startActivity(Intent.createChooser(emailIntent,"Escoger aplicacion:"))
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*
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
*/

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateReportDialogFragment().apply {

            }
    }
}
