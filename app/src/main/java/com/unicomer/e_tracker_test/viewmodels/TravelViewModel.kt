package com.unicomer.e_tracker_test.viewmodels

import android.util.Log
import java.lang.Integer
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unicomer.e_tracker_test.Adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.data.AppDatabase
import com.unicomer.e_tracker_test.data.RecordRepository
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.data.Travel
import com.unicomer.e_tracker_test.data.TotalData

class TravelViewModel: ViewModel() {

    //accediendo a los datos de firebase
    private val FirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser


    val databaseReference = FirebaseFirestore.getInstance()
    var travelReference: CollectionReference = databaseReference.collection("e-Tracker")

    //Instancia del Adapter para el RecyclerView
    var homeAdapter: AdapterHomeTravel? = null

    var data: MutableLiveData<MutableList<Travel>> = MutableLiveData()
    var dataId: MutableLiveData<String>? = MutableLiveData()
    var totalData: MutableLiveData<TotalData>? = MutableLiveData()

    var record: MutableLiveData<MutableList<Record>> = MutableLiveData()
    var recordSelected: MutableLiveData<Record> = MutableLiveData()

    var balance: ObservableField<String> = ObservableField("")

    init {

        homeAdapter = AdapterHomeTravel(this, adapterInit())

        defaultData()

    }


    fun defaultData () {
        travelReference
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            .whereEqualTo("active", true).addSnapshotListener{ querySnapshot, _ ->
                dataId?.postValue(querySnapshot!!.documents[0].id)
            }
    }


    fun setRecordData(position: Int) {
        recordSelected.postValue(record.value?.get(position))
    }


    /**
     * Inicializador para el adapter del recyclerview
     *
     * @return
     */
    private fun adapterInit(): FirestoreRecyclerOptions<Record> {
        // Se le pone "dummyData" por que no nos interesa que jale datos desde firebase ya que para
        // eso necesitamos el id
        val query: Query = travelReference.document("dummyData")
            .collection("record").orderBy("recordDate", Query.Direction.ASCENDING)
        return FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
    }


    fun initializeData(){ //metodo para llenar la cabecera de info del viaje y el recycler

        travelReference
            //verifica que el document sea del usuario
            .whereEqualTo("emailUser", FirebaseUser!!.email)
            //verifica que este esté como activo (el viaje)
            .whereEqualTo("active", true)
            .get()
            .addOnSuccessListener { doc ->

                data.postValue(doc.toObjects(Travel::class.java))

                travelReference.document(dataId?.value!!)
                    .collection("record")
                    .get()
                    .addOnSuccessListener {querySnapShot -> //obtengo todos los registros de gastos del viaje

                        record.postValue(doc.toObjects(Record::class.java))

                        for (i in 0 until querySnapShot.count()){ //count me da el TotalData de registros

                            //verifco la categoria a la que pertecene cada gasto
                            when (querySnapShot.documents[i].data!!["recordCategory"].toString()) {
                                "0" -> //si es comida acumula su cantidad en una variable
                                    totalData?.postValue(TotalData("",
                                        querySnapShot.documents[i].data!!["recordMount"].toString(),
                                        "0", "0"))
                                "1" -> // transporte
                                    totalData?.postValue(TotalData("0", "0",
                                        "0", querySnapShot.documents[i].data!!["recordMount"].toString()))
                                "2" -> //hospedaje
                                    totalData?.postValue(TotalData(querySnapShot.documents[i].data!!["recordMount"].toString(), "0",
                                        "0", "0"))
                                "3" -> //Otros
                                    totalData?.postValue(
                                        TotalData("0", "0",
                                        "0", querySnapShot.documents[i].data!!["recordMount"].toString()))
                            }
                        }

                        //muestro el TotalData de gastos disminuidos
                        // val operation = data.value?.get(0)?.balance!!.toDouble() - totalData?.value?.hotel?.toDouble()!! - totalData?.value?.car?.toDouble()!! -totalData?.value?.food?.toDouble()!! -totalData?.value?.other?.toDouble()!!
                        // balance.set(operation.toString())
                    }
            }

    }


    fun setAdapter() {

        val query: Query = travelReference.document(dataId?.value!!)
            .collection("record").orderBy("recordDate", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()

        homeAdapter = AdapterHomeTravel(this, options)
    }


}