package com.unicomer.e_tracker_test

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.Models.Record
import com.unicomer.e_tracker_test.Models.Travel
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel.*


class HomeTravelFragment : Fragment(), ShowDataInterface {

    private var listener: OnFragmentInteractionListener? = null
    //accediendo a los datos de firebase
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")
    var idTravel: String="" //debe estar inicializado para poder usarse mas adelante
    //Instancia del Adapter para el RecyclerView
    var adapterHt: AdapterHomeTravel? = null

    //Obteniendo referencias del layout
    var originCountry: TextView?=null
    var destinyCountry: TextView?=null
    var initDate: TextView?=null
    var finishDate: TextView?=null
    var balance: TextView?=null
    //totales en cabecera
    var totalFood: TextView?=null
    //para la imagen de fondo
    var backgroundImage: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        travelRef //no mover de aqui
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            .whereEqualTo("active", true).addSnapshotListener{ querySnapshot, _ ->
                idTravel = querySnapshot!!.documents[0].id
                /*Desde aca se carga el id en la variable idTravel pero se cargará unos milisegundos
                * despues de que la peticion se complete*/
            }
        adapterHt = AdapterHomeTravel(adapterInit()) //Se inicializa por primera y unica vez al adapter como uno vacio
        return inflater.inflate(R.layout.fragment_home_travel, container, false)
    }

    override fun totalFood(total: Double) {
        totalFood!!.text = total.toString()
        Log.i("FOODFR","la comida es: ${total.toString()}")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originCountry = view.findViewById(R.id.txt_header_originCountry)
        destinyCountry = view.findViewById(R.id.txt_header_originDestiny)
        initDate = view.findViewById(R.id.txt_header_initDate)
        finishDate = view.findViewById(R.id.txt_header_finishDate)
        balance = view.findViewById(R.id.txt_header_cash)
        backgroundImage = view.findViewById(R.id.backgroundRecyclerView)
        totalFood = view.findViewById(R.id.txt_header_cat_food_total)
        Log.i("FOOD","soy el onViewCreated")
        fillForm()//metodo para llenar all de fragment (incluido el recycler)
    }

    override fun onStart() {
        super.onStart()
        adapterHt!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterHt!!.startListening()
    }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    private fun adapterInit():FirestoreRecyclerOptions<Record>{ //inicializador para el adapter del recyclerview
        val query: Query = travelRef.document("dummyData") //Se le pone "dummyData" por que no nos interesa que jale datos desde firebase ya que para eso necesitamos el id
            .collection("record").orderBy("recordName")
        return FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
    }

    private fun fillForm(){ //metodo para llenar la cabecera de info del viaje y el recycler
        var data: MutableList<Travel>
        travelRef
            .whereEqualTo("emailUser", FirebaseUser!!.email) //verifica que el document sea del usuario
            .whereEqualTo("active", true) //verifica que este esté como activo (el viaje)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObjects(Travel::class.java)
                originCountry!!.text = data[0].originCountry //seteo los datos
                destinyCountry!!.text = data[0].destinyCountry
                initDate!!.text = data[0].initialDate
                finishDate!!.text = data[0].finishDate
                balance!!.text = data[0].balance
                setUpRecyclerView(idTravel) //le mando el id del viaje a este punto la peticion ya a sido existosa

                adapterHt!!.startListening() //reinicio el listening para poder poblar el recycler
            }

    }
    private fun setUpRecyclerView(id:String){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        val query: Query = travelRef.document(id)
            .collection("record").orderBy("recordName")
        val options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
        adapterHt = AdapterHomeTravel(options) //datos reales del adapter
        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerRecord)
        recycler!!.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this.context)
        recycler.adapter = adapterHt
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
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeTravelFragment()
            }
}
