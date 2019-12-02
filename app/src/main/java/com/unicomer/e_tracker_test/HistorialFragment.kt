package com.unicomer.e_tracker_test

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
import com.unicomer.e_tracker_test.models.Travel
import com.unicomer.e_tracker_test.adapters.AdapterHistory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CollectionViewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CollectionViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialFragment : Fragment(),AdapterHistory.ShowDataInterfaceHistory {


    // TODO: Rename and change types of parameters
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
    override fun sendDetailItemhistorial(id: String) {
        listener?.sendDetailItemHistory(id)
    }
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    fun setUpRecyclerView(){
        var query:Query=collecRef
            .orderBy("initialDate", Query.Direction.DESCENDING)
            .whereEqualTo("emailUser", FirebaseUser!!.email)
        var options : FirestoreRecyclerOptions<Travel> = FirestoreRecyclerOptions.Builder<Travel>()
            .setQuery(query,Travel::class.java)
            .build()

        adapter = AdapterHistory(options,this)

        var recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerHistorial)
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

//
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
        fun sendDetailItemHistory(id:String)
       // fun atras(fr: FormularioFragment)
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
