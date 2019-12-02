package com.unicomer.e_tracker_test

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.classes.CallFragment
import com.unicomer.e_tracker_test.constants.*
import com.unicomer.e_tracker_test.dialogs.CreateReportDialogFragment
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment


class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionListener,
    AddRegistroFragment.OnFragmentInteractionListener,
    TerminosFragment.OnFragmentInteractionListener,
    HomeTravelFragment.OnFragmentInteractionListener,
    TravelRegistrationFragment.OnFragmentInteractionListener,
    CreateReportDialogFragment.OnFragmentInteractionListener,
        HistorialFragment.OnFragmentInteractionListener
{
    override fun sendDetailItemHistory(id: String) {
        //cargar hometravel de cada item de historial
        CallFragment().addFragment(this.supportFragmentManager,
            HomeTravelFragment.newInstance(id), true, true, true)
    }


    var listener: onMainActivityInterface? = null
    // Declaring FirebaseAuth components
    private var dbAuth: FirebaseAuth? = null
    private var dbFirestore: FirebaseFirestore? = null
    var dbCollectionReference: CollectionReference? = null
    // Mandar a llamar al SharedPreferences
    var sharedPreferences: SharedPreferences? = null
    // End of Declaring FirebaseAuthLocalClass components
    var adapterHt: AdapterHomeTravel? = null
    var idTravel:String=""
    var persist: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(MAIN_ACTIVITY_KEY, "In method onCreate")
        // Obtener currentUser de Firebase
        dbAuth = FirebaseAuth.getInstance()
        val user = dbAuth!!.currentUser
        sharedPreferences = this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        var idDeViajeQueVieneDeFirestore: String?
        //inicializar la toolbar
        val toolbar = this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        //instancia de firebase
        dbFirestore = FirebaseFirestore.getInstance()
        dbCollectionReference = dbFirestore!!.collection("e-Tracker")
        val splashScreen: View = findViewById(R.id.MainSplash)

            // Cargar MainFragment para Inicio de Navegacion en UI
            dbCollectionReference!! //Genera la busqueda en base al email y al estado del viaje actual
                .whereEqualTo("emailUser", user!!.email)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener {querySnapshot ->
                    //Dato curioso, encuentre o no lo que busca firebase igual devuelve una respuesta aunque sea vacia pero siempre es success
                    Log.i("ERROR2","el snapshot tiene: ${querySnapshot.documents}")
                    if (querySnapshot.documents.toString()=="[]"){ //cuando no encuentra lo que busca igual devuelve un documento vacio para llenarlo []
                        //por tanto si devuelve vacio cargará homeFragment
                        CallFragment().addFragment(this.supportFragmentManager, HomeFragment(), true, false, true)
                        splashScreen.visibility = View.GONE //la visibilidad del splash depende de cuanto tiempo esta peticion tarde

                    } else {
                        idDeViajeQueVieneDeFirestore = querySnapshot!!.documents[0].id
                        editor.putString(FIREBASE_TRAVEL_ID, idDeViajeQueVieneDeFirestore)
                        editor.apply()
                        val nuevoIdCreadoLocal = sharedPreferences!!.getString(FIREBASE_TRAVEL_ID, "")
                        //idTravel = nuevoIdCreadoLocal.toString()
                        idTravel = querySnapshot.documents[0].id
                        persist = querySnapshot.documents[0].data!!["dateRegister"].toString()//envio fecha
                        Log.i("SENDEDIT", "el persist en el main es: $persist")
                        Log.i(MAIN_ACTIVITY_KEY, idDeViajeQueVieneDeFirestore)
                        Log.i(MAIN_ACTIVITY_KEY, "El nuevo ID del viaje es $nuevoIdCreadoLocal")
                        //y si el viaje ya fue registrado cargara homeTravel
                        CallFragment().addFragment(this.supportFragmentManager,
                            HomeTravelFragment.newInstance(querySnapshot.documents[0].id),
                            true, false, false)

                        splashScreen.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    Log.i("ERROR","datos: $it")
                    //y si el viaje ya fue registrado cargara homeTravel
                    CallFragment().addFragment(this.supportFragmentManager, HomeTravelFragment(), true, false, false)
                    splashScreen.visibility = View.GONE
                }
    }

    override fun onStart() {
        super.onStart()
        Log.i(MAIN_ACTIVITY_KEY, "In method onStart")

    }

    override fun onResume() {
        super.onResume()
        Log.i(MAIN_ACTIVITY_KEY, "In method onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.i(MAIN_ACTIVITY_KEY, "In method onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.i(MAIN_ACTIVITY_KEY, "In method onStop")

    }

    override fun onRestart() {
        super.onRestart()
        //adapterHt!!.startListening()
        Log.i(MAIN_ACTIVITY_KEY, "In method onRestart")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(MAIN_ACTIVITY_KEY, "In method onDestroy")

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    //INSTANCIA PARA VER LAS OPCIONES DEL MENU
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dbAuth = FirebaseAuth.getInstance()

        // Manejar seleccion de Item en Menu (Toolbar)
        return when (item.itemId) {
            // TODO Cambiar los textos del Toast por Strings

            R.id.item_historial -> {
                // Manejar el evento en item "Historial"
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
                CallFragment().addFragment(
                    this.supportFragmentManager, HistorialFragment(),
                    true, true, true)
                true
            }

            R.id.item_terminos -> {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
                CallFragment().addFragment(
                    this.supportFragmentManager, TerminosFragment(),
                    true, true, true)
                true
            }

            R.id.item_generar -> {
                // Manejar el evento en item "Generar Reporte"
                val fm = this.supportFragmentManager
                val dialog = CreateReportDialogFragment.newInstance(idTravel, "0")
                dialog.show(fm, LOGIN_DIALOG)

                true
            }

            R.id.item_fin_viaje -> {
                // Manejar el evento en item "Finalizar Viaje"
                val fm = this.supportFragmentManager
                val dialog = CreateReportDialogFragment.newInstance(idTravel, "1")
                dialog.show(fm, LOGIN_DIALOG)
                true
            }

            R.id.item_cerrarapp -> {
                // Manejar el evento en item "Cerrar sesion"
                Toast.makeText(this@MainActivity, "La sesion ha finalizado", Toast.LENGTH_SHORT).show()
                dbAuth?.signOut()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun openRegistrationTravelFragment() {
        hideToolBarOnFragmentViewDissapears()
        CallFragment().addFragment(this.supportFragmentManager,
            TravelRegistrationFragment(), true, true, true)

    }

    override fun goBackToHomeTravelFragment(){
        showToolBarOnFragmentViewCreate()
        CallFragment().addFragment(this.supportFragmentManager,
            HomeTravelFragment.newInstance(idTravel), true, true, true)
    }

    override fun openAddRecordFragment(){
        CallFragment().addFragment(this.supportFragmentManager,
            AddRegistroFragment(), true, true, true)

    }
    override fun sendToHomeTravel(id: String) {
        showToolBarOnFragmentViewCreate()
        CallFragment().addFragment(this.supportFragmentManager,
            HomeTravelFragment.newInstance(id), true, true, true)
    }
    override fun finishTravelListener() { //finaliza el viaje desde el dialog
        CallFragment().addFragment(this.supportFragmentManager,
            HomeFragment(), true, false,false)
    }
    override fun sendEditTravel(idtravel: String) {
        CallFragment().addFragment(this.supportFragmentManager,
            TravelRegistrationFragment.newInstance(idTravel, persist), true, true, true)
    }

    override fun showToolBarOnFragmentViewCreate() {
        val toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.VISIBLE
    }

    override fun hideToolBarOnFragmentViewDissapears() {
        val toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.GONE
    }


    override fun sendDetailItemHT(obj: Record, id: String, idTravel:String) {
        //detalles de items de registros, el objeto contiene todo lo que viene del adapter
        CallFragment().addFragment(this.supportFragmentManager,
            DetailRecordFragment.newInstance(obj, id, idTravel), true, true, true)

    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    interface onMainActivityInterface{
        fun getIdOnActivity(id:String)
    }
}
