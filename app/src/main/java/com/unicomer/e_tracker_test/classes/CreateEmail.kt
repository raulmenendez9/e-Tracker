package com.unicomer.e_tracker_test.classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
    val db = FirebaseFirestore.getInstance()
    var travelRef: CollectionReference = db.collection("e-Tracker")

    fun email() {
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
                    }
                    balance = (cash!!.toDouble() - totalFoodC - totalCarC - totalhotelC - totalOtherC).toString()
                }
        }
    }
}