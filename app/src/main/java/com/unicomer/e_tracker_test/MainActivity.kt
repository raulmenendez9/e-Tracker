package com.unicomer.e_tracker_test

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.classes.CallFragment
import com.unicomer.e_tracker_test.constants.*
import com.unicomer.e_tracker_test.dialogs.CreateReportDialogFragment
import com.unicomer.e_tracker_test.dialogs.FinishtTravelDialogFragment
import com.unicomer.e_tracker_test.fragments.*
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment

class MainActivity : AppCompatActivity(),
        HomeFragment.OnFragmentInteractionListener,
        AddRecordFragment.OnFragmentInteractionListener,
        TerminosFragment.OnFragmentInteractionListener,
        HomeTravelFragment.OnFragmentInteractionListener,
        TravelRegistrationFragment.OnFragmentInteractionListener,
        DetailRecordFragment.OnFragmentInteractionListener
{



    var listener: onMainActivityInterface? = null

    // Declaring FirebaseAuth components

    private var dbAuth: FirebaseAuth? = null
    private var dbFirestore: FirebaseFirestore? = null
    var dbCollectionReference: CollectionReference? = null

    // Mandar a llamar al SharedPreferences
    var sharedPreferences: SharedPreferences? = null

    // End of Declaring FirebaseAuthLocalClass components


    // Variables para HomeTravelFragment

    var adapterHt: AdapterHomeTravel? = null
    var idTravel:String = ""




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
                        CallFragment().addFragment(this.supportFragmentManager,
                            HomeFragment(), true, false, true)
                        splashScreen.visibility = View.GONE //la visibilidad del splash depende de cuanto tiempo esta peticion tarde

                    } else {

                        idDeViajeQueVieneDeFirestore = querySnapshot!!.documents[0].id
                        editor.putString(FIREBASE_TRAVEL_ID, idDeViajeQueVieneDeFirestore)
                        editor.apply()

                        val nuevoIdCreadoLocal = sharedPreferences!!.getString(FIREBASE_TRAVEL_ID, "")
                        idTravel = nuevoIdCreadoLocal.toString()
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
                    CallFragment().addFragment(this.supportFragmentManager,
                        HomeTravelFragment(), true, false, false)
                    splashScreen.visibility = View.GONE
                }
    }

    override fun onStart() {
        super.onStart()
        //adapterHt!!.startListening()
        Log.i(MAIN_ACTIVITY_KEY, "In method onStart")

    }

    override fun onResume() {
        super.onResume()
        //adapterHt!!.startListening()
        Log.i(MAIN_ACTIVITY_KEY, "In method onResume")

    }

    override fun onPause() {
        super.onPause()
        //adapterHt!!.startListening()
        Log.i(MAIN_ACTIVITY_KEY, "In method onPause")

    }

    override fun onStop() {
        super.onStop()
        //adapterHt!!.startListening()
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
    lateinit var globalmenu: Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menus, menu)

        val searchItem=menu.findItem(R.id.action_search)
        if (searchItem!=null) {

            val searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Search"
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    // logica para filtrar con el recyclerView
                    //adaptador.getFilter().filter(newText)
                    //setUpRecyclerView(idTravel,newText)
                    //adapterHt!!.startListening()
                    Toast.makeText(this@MainActivity,"$newText",Toast.LENGTH_LONG).show()
                    return true

                }
            })

        }   else    {

            Toast.makeText(this,"no reconoce el searchview",Toast.LENGTH_LONG).show()
        }
        globalmenu = menu
        return super.
            onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dbAuth = FirebaseAuth.getInstance()

        // Manejar seleccion de Item en Menu (Toolbar)
        return when (item.itemId) {
            // TODO Cambiar los textos del Toast por Strings

            R.id.item_historial -> {
                // Manejar el evento en item "Historial"
                globalmenu.findItem(R.id.item_generar).setVisible(true)
                globalmenu.findItem(R.id.item_fin_viaje).setVisible(true)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
                CallFragment().addFragment(
                    this.supportFragmentManager,
                    HistorialFragment(),
                    true, true, true)
                true
            }

            R.id.item_terminos -> {
                // Manejar el evento en item "Terminos y Condiciones"
                globalmenu.findItem(R.id.item_generar).setVisible(true)
                globalmenu.findItem(R.id.item_fin_viaje).setVisible(true)
                CallFragment().addFragment(
                    this.supportFragmentManager,
                    TerminosFragment(),
                    true, true, true)
                true
            }

            R.id.item_generar -> {
                // Manejar el evento en item "Generar Reporte"
                val fm = this.supportFragmentManager
                val dialog = CreateReportDialogFragment.newInstance(idTravel)
                dialog.show(fm, LOGIN_DIALOG)

                true
            }

            R.id.item_fin_viaje -> {
                // Manejar el evento en item "Finalizar Viaje"
                val fm = this.supportFragmentManager
                val dialog = FinishtTravelDialogFragment.newInstance()
                dialog.show(fm, LOGIN_DIALOG)
                Toast.makeText(this@MainActivity, "item fin viaje", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@MainActivity, "ningun item", Toast.LENGTH_SHORT).show()
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
        globalmenu.findItem(R.id.item_generar).setVisible(true) //visibilidad para items del menu
        globalmenu.findItem(R.id.item_fin_viaje).setVisible(true)
        CallFragment().addFragment(this.supportFragmentManager,
            HomeTravelFragment.newInstance(idTravel), true, true, true)
    }

    override fun createNewRecord(){
        CallFragment().addFragment(this.supportFragmentManager,
            AddRecordFragment.createRecord(false), true, true, true)
    }

    override fun updateExistingRecord(objDetailData: Record, idRecord: String, idTravel: String, recordExists: Boolean) {
        CallFragment().addFragment(this.supportFragmentManager,
            AddRecordFragment.updateRecord(objDetailData, idRecord, idTravel, recordExists),
            true, true, true)
    }

    override fun sendDetailItemHT(obj: Record, id: String, idTravel:String) {
        //detalles de items de registros, el objeto contiene todo lo que viene del adapter
        CallFragment().addFragment(this.supportFragmentManager,
            DetailRecordFragment.newInstance(obj, id, idTravel), true, true, true)

    }

    override fun sendToHomeTravel(id: String) {
        showToolBarOnFragmentViewCreate()
        globalmenu.findItem(R.id.item_generar).setVisible(true)
        globalmenu.findItem(R.id.item_fin_viaje).setVisible(true)
        Log.i("BUSCANDOID","el id en el activity es: $id")
        CallFragment().addFragment(this.supportFragmentManager,
            HomeTravelFragment.newInstance(id), true, true, true)
    }

    override fun showToolBarOnFragmentViewCreate() {
        val toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.VISIBLE
    }

    override fun hideToolBarOnFragmentViewDissapears() {
        val toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.GONE
    }


    override fun onFragmentInteraction(uri: Uri) {
    }

    interface onMainActivityInterface{
        fun getIdOnActivity(id:String)
    }
}
