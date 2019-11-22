package com.unicomer.e_tracker_test.travel_registration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unicomer.e_tracker_test.Models.HistoryTravel
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.adapters.AdapterHistoryTravel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HistoryTravelFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")
    var adapterHt: AdapterHistoryTravel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapterHt= AdapterHistoryTravel(adapterInit())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        adapterHt= AdapterHistoryTravel(adapterInit())


        return inflater.inflate(R.layout.fragment_history_travel, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        adapterHt!!.startListening()
    }
    override fun onStart() {
        super.onStart()
        adapterHt!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterHt!!.startListening()
    }

    private fun adapterInit():FirestoreRecyclerOptions<HistoryTravel>{ //inicializador para el adapter del recyclerview
        val query: Query = travelRef.document("dummyData") //Se le pone "dummyData" por que no nos interesa que jale datos desde firebase ya que para eso necesitamos el id
            .collection("record").orderBy("recordName")

        return FirestoreRecyclerOptions.Builder<HistoryTravel>()
            .setQuery(query, HistoryTravel::class.java)
            .build()
    }


    private fun setUpRecyclerView(){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        val query: Query = travelRef.orderBy("initialDate",Query.Direction.DESCENDING)
            //.whereEqualTo("emailUser",FirebaseUser!!.email)

            //.whereEqualTo("recordName",consulta)


        val options: FirestoreRecyclerOptions<HistoryTravel> = FirestoreRecyclerOptions.Builder<HistoryTravel>()
            .setQuery(query, HistoryTravel::class.java)
            .build()
        adapterHt = AdapterHistoryTravel(options) //datos reales del adapter
        //Log.i("consulta","${adapterHt!!.itemCount}")
        var recycler = view?.findViewById<RecyclerView>(R.id.recyclerHistory)
        recycler!!.setHasFixedSize(true)
        recycler!!.layoutManager = LinearLayoutManager(this.context)
        recycler!!.adapter = adapterHt
    }

    // TODO: Rename method, update argument and hook method into UI event

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryTravelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
