package com.unicomer.e_tracker_test.classes

import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.opencsv.CSVWriter
import java.io.FileWriter
import android.R.attr.data
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.unicomer.e_tracker_test.models.Record
import java.io.File
import java.util.*


class CreateEmail(idtravel: String) {
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
    var recordName: String?=null
    var recordDate: String?=null
    var recordMount: String?=null
    var recordCategory:String?=null
    var recordPhoto:String?=null
    var recordDescription: String?=null
    var totalFoodC = 0.0
    var totalCarC = 0.0
    var totalhotelC = 0.0
    var totalOtherC = 0.0
    val idTravel =idtravel
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")

    fun email(){
        travelRef.document(idTravel).get().addOnSuccessListener {
            val csv =(Environment.getExternalStorageDirectory().absolutePath + "/Travel_${FirebaseUser!!.email}.csv")//Nombre del archivo.csv
            var write:CSVWriter?=null
            write = CSVWriter(FileWriter(csv))
            val dataHeader = arrayOf<String>("VIAJE", "Realizado por","${FirebaseUser.email}")
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
            val dataHeaderTravelcsv = arrayOf("Pais origen", "Pais destino","Centro de costo",
                "Efectivo asignado","Reintegro","Fecha inicio","Fecha fin","Aprovado por","Descripción")
            val dataTravel= arrayOf(originCountry,destinyCountry,centerCost,cash,
                refund,initialDate,finishDate,aproved,description)
            val dataHeaderRecord = arrayOf("REGISTROS")
            val dataHeaderRecords = arrayOf("Nombre","Fecha","Costo","Categoria","Enlace de imagen","Descripcion")
            val dataTotal = arrayOf("TOTAL DE GASTOS","","$balance")
            write!!.writeNext(dataHeader)
            write!!.writeNext(dataHeaderTravelcsv)
            write!!.writeNext(dataTravel)
            write!!.writeNext(dataHeaderRecord)
            write!!.writeNext(dataHeaderRecords)

            travelRef.document(idTravel).collection("record").get()
                .addOnSuccessListener { querySnapShot ->
                    var write2:CSVWriter?=null
                    write2 = CSVWriter(FileWriter(csv))
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
                        Log.d("FOR", "$dataRecords")
                        write2!!.writeNext(dataRecords)
                    }

                }
            balance = (cash!!.toDouble() - totalFoodC - totalCarC - totalhotelC - totalOtherC).toString()

            write!!.writeNext(dataTotal)
            write!!.close()
            Log.d("CREATE_EMAIL", "$originCountry")




            //envio por correo
            var emailIntent = Intent(Intent.ACTION_SEND)

            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("raul@unicomer.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Asunto")
            emailIntent.putExtra(Intent.EXTRA_TEXT,"Escriba aqui")
            emailIntent.type
            var file = File(csv)
            var uri = Uri.fromFile(file)
            emailIntent.putExtra(Intent.EXTRA_STREAM,uri)
            Log.d("EMAIL", "$uri")
        }


        //return emailIntent
        //startActivity(Intent.createChooser(emailIntent,"Escoje una aplicacion de email"))
    }
}