package com.unicomer.e_tracker_test.fragments

import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.classes.CallFragment
import com.unicomer.e_tracker_test.models.Travel
import com.unicomer.e_tracker_test.models.Record


class HomeTravelFragment : Fragment(),
    AdapterHomeTravel.ShowDataInterface{
    private var listener: OnFragmentInteractionListener? = null
    //accediendo a los datos de firebase
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")
    private lateinit var idTravelMain:String //variable que recibe el id
    private lateinit var esActual:String //viaje actual: 0=si, 1=no
    private lateinit var persist: String
    //Instancia del Adapter para el RecyclerView
    var adapterHt: AdapterHomeTravel? = null
    //Obteniendo referencias del layout
    var originCountry: TextView?=null
    var destinyCountry: TextView?=null
    var initDate: TextView?=null
    var finishDate: TextView?=null
    var balance: TextView?=null
    //Edit Button travel

    var editBtn: ImageView?=null


    private var floatingActionButton: FloatingActionButton? = null
    private var floatingActionButtonSendReport: FloatingActionButton?=null
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
        //Se inicializa por primera y unica vez al adapter como uno vacio
        setHasOptionsMenu(true) //menu
        adapterHt = AdapterHomeTravel(adapterInit(), this)
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
        floatingActionButton = view.findViewById(R.id.floatingActionButtonHomeTravel)
        floatingActionButtonSendReport = view.findViewById(R.id.floatingActionButtonHTCreateReport)
        editBtn = view.findViewById(R.id.id_editTravel_imageView)
        //viaje actual: 0=si, 1=no


        if(esActual!="0"){
            editBtn!!.visibility = View.GONE
            floatingActionButton!!.visibility = View.GONE
            floatingActionButtonSendReport!!.visibility = View.VISIBLE
        }


        fillForm()//metodo para llenar la cabecera de fragment

        setUpRecyclerView(idTravelMain, "") //llena el fragment
        editBtn!!.setOnClickListener {
            Log.i("PERSIST", "lsitener del edit tiene: $persist")
            listener!!.sendEditTravel(idTravelMain, persist)
        }
        floatingActionButton?.setOnClickListener {

            listener?.createNewRecord()

        }
        floatingActionButtonSendReport?.setOnClickListener {
            listener!!.sendCreateRportDialog(idTravelMain, "0")
        }

    }

    override fun sendDetailItem(Obj: Record, id:String) {
        listener!!.sendDetailItemHT(Obj, id, idTravelMain, esActual)
    }

    override fun openAddRecordFragment(Obj: Record, id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        val query: Query = travelRef.document(idTravelMain)
            .collection("record").orderBy("recordDate", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
    }

    private fun fillForm(){ //metodo para llenar la cabecera de info del viaje y el recycler
        var data: Travel?
        var totalFoodC = 0.0
        var totalCarC = 0.0
        var totalHotelC = 0.0
        var totalOtherC = 0.0
        travelRef
            .document(idTravelMain)
            .get()
            .addOnSuccessListener { doc ->
                //data = doc.data.toMutableMap()
                data = doc.toObject(Travel::class.java)
                originCountry!!.text = data!!.originCountry //seteo los datos
                destinyCountry!!.text = data!!.destinyCountry
                initDate!!.text = data!!.initialDate!!.substring(0, data!!.initialDate!!.length-5)
                finishDate!!.text = data!!.finishDate!! //.substring(0, data[0].initialDate!!.length-5)
                //persist = data!!.DateRegister.toString()
                Log.i("PERSIST", "dentro de fillform en HomeTravelFragment(), persist: $persist")
                //llenar los totales
                travelRef.document(idTravelMain)
                    .collection("record")
                    .get()
                    .addOnSuccessListener {querySnapShot -> //obtengo todos los registros de gastos del viaje
                        if (querySnapShot.count()==0){ //obtengo el tamaño de los datos del recycler si es distinto a cero quita la imagen de fondo
                            backgroundImage!!.visibility = View.VISIBLE
                        }
                        for (i in 0 until querySnapShot.count()){ //count me da el total de registros
                            when (querySnapShot.documents[i].data!!["recordCategory"].toString()) { //verifco la categoria a la que pertecene cada gasto
                                "0" -> //si es comida acumula su cantidad en una variable
                                    totalFoodC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "1" -> // transporte
                                    totalCarC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "2" -> //hospedaje
                                    totalHotelC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                                "3" -> //Otros
                                    totalOtherC += querySnapShot.documents[i].data!!["recordMount"].toString().toDouble()
                            }
                        }
                        totalFood!!.text = "$$totalFoodC"
                        totalCar!!.text = "$$totalCarC"
                        totalHotel!!.text = "$$totalHotelC"
                        totalOther!!.text = "$$totalOtherC"
                        //muestro el total de gastos disminuidos
                        val balanceReg = data!!.balance!!.toDouble() - totalFoodC - totalCarC -totalHotelC -totalOtherC
                        val roundedBalance = Math.round(balanceReg*100.0)/100.0
                        balance!!.text = roundedBalance.toString()
                    }
            }

    }
    private fun setUpRecyclerView(id:String, newText:String){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        val query: Query = if (newText==""){
                travelRef.document(id)
                .collection("record")
                .orderBy("recordDateRegister", Query.Direction.DESCENDING)
            }else{
                travelRef.document(id)
                .collection("record")
                .orderBy("recordName")
                .startAt(newText).endAt(newText+"\uf8ff")
                }
        val options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
        adapterHt = AdapterHomeTravel(options, this) //datos reales del adapter
        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerRecord)
        recycler!!.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this.context)
        recycler.adapter = adapterHt
    }

    //Busqueda de items en recyclerView
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menus, menu)
        val searchItem= menu.findItem(R.id.action_search)
        if(searchItem!=null){
            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Buscar..."
            searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String): Boolean {
                    setUpRecyclerView(idTravelMain, newText)
                    adapterHt!!.startListening()
                    //Toast.makeText(context,"desde el fragment: $newText", Toast.LENGTH_LONG).show()
                    return true
                }
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })
        }
         super.onCreateOptionsMenu(menu, inflater)
    }
    //INSTANCIA DEL MENU
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.item_generar).isVisible= true
        menu.findItem(R.id.item_fin_viaje).isVisible = esActual == "0"
    }
    //FIN INSTANCIA DEL MENU
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
        fun createNewRecord()
        fun onFragmentInteraction(uri: Uri)
        fun goBackToHomeTravelFragment()
        fun showToolBarOnFragmentViewCreate()
        fun sendDetailItemHT(obj:Record, id: String, idTravel:String, esActual: String)
        fun sendEditTravel(idtravel:String, persist:String?)
        fun sendCreateRportDialog(idTravel: String, whichLayout:String)
    }

    companion object {
        @JvmStatic
        fun newInstance(idTravel: String, esActual:String, persist: String): HomeTravelFragment{
            val fragment = HomeTravelFragment()
            fragment.idTravelMain = idTravel
            fragment.esActual = esActual
            fragment.persist = persist
            return fragment
            }
    }
}
