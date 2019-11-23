package com.unicomer.e_tracker_test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.unicomer.e_tracker_test.Adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.models.Travel
import com.unicomer.e_tracker_test.models.Record


class HomeTravelFragment : Fragment(){

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

    private var floatingActionButton: FloatingActionButton? = null

    //totales en cabecera
    var totalFood: TextView?=null
    var totalCar: TextView?=null
    var totalHotel: TextView?=null
    var totalOther: TextView?=null
    //para la imagen de fondo
    var backgroundImage: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Mostrar el toolbar
        listener?.showToolBarOnFragmentViewCreate()


        travelRef //no mover de aqui
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            .whereEqualTo("active", true).addSnapshotListener{ querySnapshot, _ ->
                idTravel = querySnapshot!!.documents[0].id
                /*Desde aca se carga el id en la variable idTravel pero se cargará unos milisegundos
                * despues de que la peticion se complete*/
            }
        adapterHt = AdapterHomeTravel(adapterInit()) //Se inicializa por primera y unica vez al adapter como uno vacio

        floatingActionButton = view?.findViewById(R.id.floatingActionButtonHomeTravel)
        floatingActionButton?.setOnClickListener {
            listener?.openAddRecordFragment()
        }


        return inflater.inflate(R.layout.fragment_home_travel, container, false)
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
        totalCar = view.findViewById(R.id.txt_header_cat_car_total)
        totalHotel = view.findViewById(R.id.txt_header_cat_hotel_total)
        totalOther = view.findViewById(R.id.txt_header_cat_other_total)
        fillForm()//metodo para llenar all de fragment (incluido el recycler)
    }

    override fun onStart() {
        super.onStart()
        adapterHt!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapterHt!!.stopListening()
    }

    override fun onPause() {
        super.onPause()
        adapterHt!!.stopListening()
    }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    private fun adapterInit():FirestoreRecyclerOptions<Record>{ //inicializador para el adapter del recyclerview
        val query: Query = travelRef.document("dummyData") //Se le pone "dummyData" por que no nos interesa que jale datos desde firebase ya que para eso necesitamos el id
            .collection("record").orderBy("recordDate", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
    }

    private fun fillForm(){ //metodo para llenar la cabecera de info del viaje y el recycler
        var data: MutableList<Travel>
        var totalFoodC = 0.0
        var totalCarC = 0.0
        var totalhotelC = 0.0
        var totalOtherC = 0.0
        travelRef
            .whereEqualTo("emailUser", FirebaseUser!!.email) //verifica que el document sea del usuario
            .whereEqualTo("active", true) //verifica que este esté como activo (el viaje)
            .get()
            .addOnSuccessListener { doc ->
                data = doc.toObjects(Travel::class.java)
                originCountry!!.text = data[0].originCountry //seteo los datos
                destinyCountry!!.text = data[0].destinyCountry
                initDate!!.text = data[0].initialDate!!.substring(0, data[0].initialDate!!.length-5)
                finishDate!!.text = data[0].finishDate!!.substring(0, data[0].initialDate!!.length-5)
                //balance!!.text = data[0].balance
                setUpRecyclerView(idTravel) //le mando el id del viaje a este punto la peticion ya a sido existosa
                adapterHt!!.startListening() //reinicio el listening para poder poblar el recycler
                //llenar los totales
                travelRef.document(idTravel)
                    .collection("record")
                    .get()
                    .addOnSuccessListener {querySnapShot -> //obtengo todos los registros de gastos del viaje
                        for (i in 0 until querySnapShot.count()){ //count me da el total de registros
                            when (querySnapShot.documents[i].data!!["recordCategory"].toString()) { //verifco la categoria a la que pertecene cada gasto
                                "0" -> //si es comida acumula su cantidad en una variable
                                    totalFoodC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "1" -> // transporte
                                    totalCarC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "2" -> //hospedaje
                                    totalhotelC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "3" -> //Otros
                                    totalOtherC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                            }
                        }
                        totalFood!!.text = "$totalFoodC"
                        totalCar!!.text = "$totalCarC"
                        totalHotel!!.text = "$totalhotelC"
                        totalOther!!.text = "$totalOtherC"
                        //muestro el total de gastos disminuidos
                        val balanceReg = data[0].balance!!.toDouble() - totalFoodC - totalCarC -totalhotelC -totalOtherC
                        balance!!.text = balanceReg.toString()
                    }
            }

    }
    private fun setUpRecyclerView(id:String){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        val query: Query = travelRef.document(id)
            .collection("record").orderBy("recordDate", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
        query.get().addOnSuccessListener {doc->
            if (doc.count()!=0){ //obtengo el tamaño de los datos del recycler si es distinto a cero quita la imagen de fondo
                backgroundImage!!.visibility = View.GONE
            }
        }
        adapterHt = AdapterHomeTravel(options) //datos reales del adapter
        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerRecord)
        recycler!!.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this.context)
        recycler.adapter = adapterHt
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

    private fun floatingActionButtonHomeTravel(goToHomeTravelFragment: HomeTravelFragment){
        //TODO Needs to call AddRecordFragment
    }


    interface OnFragmentInteractionListener {
        fun openAddRecordFragment()
        fun onFragmentInteraction(uri: Uri)
        fun goBackToHomeTravelFragment()
        fun showToolBarOnFragmentViewCreate()
    }

    companion object {

        @JvmStatic
        fun newInstance() = HomeTravelFragment()
            }
}
