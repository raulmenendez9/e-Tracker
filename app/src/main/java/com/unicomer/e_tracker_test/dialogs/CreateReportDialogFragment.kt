package com.unicomer.e_tracker_test.dialogs

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import com.opencsv.CSVWriter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Record
import java.io.File
import java.io.FileWriter


class CreateReportDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    var btnCancel: Button? = null
    var btnSend:Button?=null
    private var listener: OnFragmentInteractionListener? = null
    var originCountry: String?=null
    var destinyCountry: String?=null
    var centerCost: String?=null
    var cash: String?=null
    var emailUser: String?=null
    var refund: String?=null
    var initialDate: String?=null
    var finishDate: String?=null
    var aproved: String?=null
    var description: String?=null
    var balance: String?=null
    var totalFoodC = 0.0
    var totalCarC = 0.0
    var totalhotelC = 0.0
    var totalOtherC = 0.0
    private lateinit var  idtravel :String
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")

    //decision sobre que layout usar
    //whichLayout == 0 -> Create Report layout
    //whichLayout != 0 -> Finish travel layout
    private lateinit var whichLayout: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (whichLayout=="0"){
            inflater.inflate(R.layout.fragment_create_report_dialog, container, false)
        }else{
            inflater.inflate(R.layout.fragment_finisht_travel_dialog, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(whichLayout=="0"){
            btnCancel = view.findViewById(R.id.button_cancel2)
            btnSend = view.findViewById(R.id.button_send2)
        }else{
            btnCancel = view.findViewById(R.id.button_cancelTravel)
            btnSend = view.findViewById(R.id.button_finishTravel)
        }
    }

    override fun onResume() {
        super.onResume()
        btnCancel!!.setOnClickListener {
            dialog!!.cancel()
        }
        if(whichLayout=="0"){
            btnSend!!.setOnClickListener {
                email(idtravel)
                dialog!!.cancel()
            }
        }else{
            btnSend!!.setOnClickListener {
                finishTravel(idtravel)
                dialog!!.cancel()
            }
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    fun email(idTravel: String){
        travelRef.document(idTravel).get().addOnSuccessListener {

            originCountry = it.data?.get("originCountry")?.toString()
            destinyCountry = it.data?.get("destinyCountry")?.toString()
            centerCost = it.data?.get("centerCost")?.toString()
            cash = it.data?.get("cash")?.toString()
            emailUser = it.data?.get("emailUser")?.toString()
            refund = it.data?.get("refund")?.toString()
            initialDate = it.data?.get("initialDate")?.toString()
            finishDate = it.data?.get("finishDate")?.toString()
            aproved = it.data?.get("aproved")?.toString()
            description = it.data?.get("description")?.toString()
            //balance = it.data?.get("balance")?.toString()
            travelRef.document(idTravel).collection("record").get()
                .addOnSuccessListener { querySnapShot ->
                    val csv =(Environment.getExternalStorageDirectory().absolutePath + "/Travel_${FirebaseUser!!.email}.csv")//Nombre del archivo.csv
                    var write:CSVWriter?=null
                    write = CSVWriter(FileWriter(csv))
                    val dataHeader = arrayOf<String>("VIAJE", "Realizado por","${FirebaseUser.email}")
                    val dataHeaderTravelcsv = arrayOf("Pais origen", "Pais destino","Centro de costo",
                        "Efectivo asignado","Reintegro","Fecha inicio","Fecha fin","Aprovado por","Descripción")
                    val dataTravel= arrayOf(originCountry,destinyCountry,centerCost,cash,
                        refund,initialDate,finishDate,aproved,description)
                    val dataHeaderRecord = arrayOf("REGISTROS")
                    val dataHeaderRecords = arrayOf("Nombre","Fecha","Costo","Categoria","Enlace de imagen","Descripcion")

                    write!!.writeNext(dataHeader)
                    write!!.writeNext(dataHeaderTravelcsv)
                    write!!.writeNext(dataTravel)
                    write!!.writeNext(dataHeaderRecord)
                    write!!.writeNext(dataHeaderRecords)
                    val records = querySnapShot.toObjects(Record::class.java)
                    var dataRecords = arrayOf(String())
                    //obtengo todos los registros de gastos del viaje
                    for (i in 0 until querySnapShot.count()) { //count me da el total de registros
                        when (querySnapShot.documents[i].data!!["recordCategory"].toString()) { //verifco la categoria a la que pertecene cada gasto
                            "0" -> //si es comida acumula su cantidad en una variable
                                totalFoodC += querySnapShot.documents[i].data!!["recordMount"].toString()
                                    .toDouble()
                            "1" -> // transporte
                                totalCarC += querySnapShot.documents[i].data!!["recordMount"].toString()
                                    .toDouble()
                            "2" -> //hospedaje
                                totalhotelC += querySnapShot.documents[i].data!!["recordMount"].toString()
                                    .toDouble()
                            "3" -> //Otros
                                totalOtherC += querySnapShot.documents[i].data!!["recordMount"].toString()
                                    .toDouble()
                        }
                        dataRecords = arrayOf(
                            records[i].recordName,
                            records[i].recordDate,
                            records[i].recordMount,
                            records[i].recordCategory,
                            records[i].recordPhoto,
                            records[i].recordDescription)
                        write!!.writeNext(dataRecords)
                    }
                    balance = (cash!!.toDouble() - totalFoodC - totalCarC - totalhotelC - totalOtherC).toString()
                    val dataTotal = arrayOf("TOTAL DE GASTOS","","$balance")
                    write!!.writeNext(dataTotal)
                    write!!.close()

                    //envio por correo
                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Asunto")
                    emailIntent.putExtra(Intent.EXTRA_TEXT,"Escriba aqui")
                    emailIntent.type = "message/rfc822"
                    var file = File(csv)
                    var uri = FileProvider.getUriForFile(context!!,context!!.applicationContext.packageName+".fileprovider",file)
                    emailIntent.putExtra(Intent.EXTRA_STREAM,uri)
                    emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    Log.d("EMAIL", "$uri")
                    val activities:List<ResolveInfo> = context!!.packageManager.queryIntentActivities(
                        emailIntent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                    val isIntentSafe:Boolean = activities.isNotEmpty()
                    if (isIntentSafe) {
                        startActivity(Intent.createChooser(emailIntent, "Escoger una aplicacion:"))
                    }
                }
        }

    }
    fun finishTravel(id:String){
        travelRef
            .document(id)
            .update("active", false)
            .addOnSuccessListener {
                email(id) //se genera el reporte
                listener!!.finishTravelListener() //manda a homeFragment
            }
            .addOnFailureListener {
                    e -> Log.w("ERROR", "Error updating finish Travel on CreateReportDialog", e)
            }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
        fun finishTravelListener()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(idTavel:String, whichLayout:String):CreateReportDialogFragment{
            val fragment=CreateReportDialogFragment()
            fragment.idtravel=idTavel
            fragment.whichLayout=whichLayout
            return fragment
        }

    }
}
