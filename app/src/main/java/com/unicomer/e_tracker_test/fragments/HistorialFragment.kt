package com.unicomer.e_tracker_test.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Travel
import com.unicomer.e_tracker_test.adapters.AdapterHistory
import com.unicomer.e_tracker_test.adapters.AdapterHistory.historyAdapterInterface

class HistorialFragment : Fragment(), historyAdapterInterface {




    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var listener: OnFragmentInteractionListener? = null
    val db= FirebaseFirestore.getInstance()
    var collecRef: CollectionReference = db.collection("e-Tracker")
    var adapter:AdapterHistory?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }


    fun onButtonPressed() {
        //listener?.onFragmentInteraction(uri)
    }

    fun setUpRecyclerView(){
        val query:Query=collecRef
            .orderBy("initialDate", Query.Direction.DESCENDING)
            .whereEqualTo("emailUser", FirebaseUser!!.email)
        val options : FirestoreRecyclerOptions<Travel> = FirestoreRecyclerOptions.Builder<Travel>()
            .setQuery(query,Travel::class.java)
            .build()

        adapter = AdapterHistory(options, this)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerHistorial)
        recyclerView!!.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter=adapter
    }

    override fun onStart(){
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop(){
        super.onStop()
        adapter!!.startListening()
    }

   //ADMIN DEL MENU DE LA TOOLBAR
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menus, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.item_generar).isVisible= false
        menu.findItem(R.id.item_fin_viaje).isVisible= false
        menu.findItem(R.id.action_search).isVisible=false
        menu.findItem(R.id.item_historial).isVisible=false
    }
    //FIN DEL ADMIN DEL MENU DE LA TOOLBAR
    override fun sendDataToHistoryTtoHomeT(idTravel: String) {
        listener!!.sendDatatoHomeTfromHistT(idTravel, "1")
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

        fun onFragmentInteraction(uri: Uri)
        fun sendDatatoHomeTfromHistT(idTravel:String, esActual:String)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HistorialFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
