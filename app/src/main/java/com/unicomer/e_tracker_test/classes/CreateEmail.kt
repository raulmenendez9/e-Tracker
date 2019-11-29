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
import androidx.core.content.ContextCompat.startActivity
import java.io.File


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

    fun email() :Intent{
        val csv =(Environment.getExternalStorageDirectory().absolutePath + "/Travel_${FirebaseUser!!.email}.csv")//Nombre del archivo.csv
        var write:CSVWriter?=null
        write = CSVWriter(FileWriter(csv))
        val data = arrayListOf<String>()
        travelRef.document(idTravel).get().addOnSuccessListener {
            originCountry = it.data?.get("originContry")?.toString()
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
            data.add(arrayOf("VIAJE", "Realizado por",emailUser).toString())
            data.add(arrayOf("Pais origen", "Pais destino","Centro de costo",
                "Efectivo asignado","Efectivo usado","Reintegro","Fecha inicio","Fecha fin","Aprovado por","Descripción").toString())
            data.add(arrayOf(originCountry,destinyCountry,centerCost,cash,
                balance,refund,initialDate,finishDate,aproved,description).toString())
            data.add(arrayOf("REGISTROS").toString())
            data.add(arrayOf("Nombre","Fecha","Costo","Categoria","Enlace de imagen","Descripcion").toString())

            travelRef.document(idTravel).collection("record").get()
                .addOnSuccessListener { querySnapShot ->
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
                        recordName = querySnapShot.documents[i].data!!["recordName"].toString()
                        recordDate =querySnapShot.documents[i].data!!["recordDate"].toString()
                        recordMount=querySnapShot.documents[i].data!!["recordMount"].toString()
                        recordCategory=querySnapShot.documents[i].data!!["recordCategory"].toString()
                        recordPhoto=querySnapShot.documents[i].data!!["recordPhoto"].toString()
                        recordDescription=querySnapShot.documents[i].data!!["recordDescription"].toString()
                        data.add(arrayOf(recordName,recordDate,recordMount,recordCategory,recordPhoto,
                            recordDescription).toString())
                    }

                }
            balance = (cash!!.toDouble() - totalFoodC - totalCarC - totalhotelC - totalOtherC).toString()
            data.add(arrayOf("TOTAL",balance).toString())

        }
        write.writeAll(data as List<Array<String>>)
        write.close()

        //envio por correo
        var emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Asunto")
        emailIntent.putExtra(Intent.EXTRA_TEXT,"")
        var file = File(csv)
        var uri = Uri.fromFile(file)
        emailIntent.putExtra(Intent.EXTRA_STREAM,uri)
        return emailIntent
        //startActivity(Intent.createChooser(emailIntent,"Escoje una aplicacion de email"))
    }
}