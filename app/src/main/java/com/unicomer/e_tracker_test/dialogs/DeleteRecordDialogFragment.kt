package com.unicomer.e_tracker_test.dialogs

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unicomer.e_tracker_test.HomeTravelFragment

import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Record


class DeleteRecordDialog : DialogFragment() {
    lateinit var idRecord:String
    lateinit var idTravel: String
    lateinit var objRecord: Record
    private var listener: OnFragmentInteractionListener? = null
    //FIREBASE
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")
    //UI
    var btnDelete: Button? =null
    var btnCancel: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_record_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDelete = view.findViewById(R.id.button_send)
        btnCancel = view.findViewById(R.id.button_cancel)
    }

    override fun onResume() {
        super.onResume()
        btnCancel!!.setOnClickListener {
            dialog!!.cancel()
        }
        btnDelete!!.setOnClickListener {
            val storageRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(objRecord.recordPhoto)
            storageRef //borra foto
                .delete()
            travelRef
                .document(idTravel)
                .collection("record")
                .document(idRecord)
                .delete()
                .addOnSuccessListener {
                    //FRAGMENT MANAGER
                    val fragmentHT = HomeTravelFragment.newInstance(idTravel)
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.replace(R.id.main_fragment_container, fragmentHT)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    dialog!!.cancel()
                }
                .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
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
        @JvmStatic
        fun newInstance(id: String, idTravel:String, obj:Record): DeleteRecordDialog{
            //recibe el id del registro a borrar
            val fragment = DeleteRecordDialog()
            fragment.idRecord = id
            fragment.idTravel = idTravel
            fragment.objRecord = obj
            return fragment
        }
    }
}
