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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unicomer.e_tracker_test.adapters.AdapterHomeTravel
import com.unicomer.e_tracker_test.constants.*
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.Classes.CallFragment
import com.unicomer.e_tracker_test.Constants.*

import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment

class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionListener,
    AddRegistroFragment.OnFragmentInteractionListener,
    TerminosFragment.OnFragmentInteractionListener,
    HomeTravelFragment.OnFragmentInteractionListener
{


    // Declaring FirebaseAuthLocalClass components

    private var dbAuth: FirebaseAuth? = null
    private var dbFirestore: FirebaseFirestore? = null
    var dbCollectionReference: CollectionReference? = null

    // Mandar a llamar al SharedPreferences
    var sharedPreferences: SharedPreferences? = null

    // End of Declaring FirebaseAuthLocalClass components
    var adapterHt: AdapterHomeTravel? = null
    var idTravel:String=""
    val idd: String = ""
    val dateinit: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(MAIN_ACTIVITY_KEY, "In method onCreate")


        // Obtener currentUser de Firebase

        dbAuth = FirebaseAuth.getInstance()
        val user = dbAuth!!.currentUser
        sharedPreferences = this.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
        var editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        var idDeViajeQueVieneDeFirestore: String? = null


        //inicializar la toolbar

        val toolbar = this.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //instancia de firebase

        dbFirestore = FirebaseFirestore.getInstance()
        dbCollectionReference = dbFirestore!!.collection("e-Tracker")
        var splashScreen: View = findViewById(R.id.MainSplash)




            // Cargar MainFragment para Inicio de Navegacion en UI

            dbCollectionReference = dbFirestore?.collection("e-Tracker")

            dbCollectionReference!! //Genera la busqueda en base al email y al estado del viaje actual
                .whereEqualTo("emailUser", user!!.email)
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener {querySnapshot ->
                    //idTravel = querySnapshot!!.documents[0].id
                    //Dato curioso, encuentre o no lo que busca firebase igual devuelve una respuesta aunque sea vacia pero siempre es success
                    Log.i("ERROR2","el snapshot tiene: ${querySnapshot.documents}")
                    if (querySnapshot.documents.toString()=="[]"){ //cuando no encuentra lo que busca igual devuelve un documento vacio para llenarlo []
                        addFragment(HomeFragment()) //por tanto si devuelve vacio cargará homeFragment
                        splashScreen.visibility = View.GONE //la visibilidad del splash depende de cuanto tiempo esta peticion tarde

                    } else {

                        idDeViajeQueVieneDeFirestore = querySnapshot!!.documents[0].id
                        editor.putString(FIREBASE_TRAVEL_ID, idDeViajeQueVieneDeFirestore)
                        editor.apply()

                        var nuevoIdCreadoLocal = sharedPreferences!!.getString(FIREBASE_TRAVEL_ID, "")
                        idTravel = nuevoIdCreadoLocal.toString()
                        Log.i(MAIN_ACTIVITY_KEY, idDeViajeQueVieneDeFirestore)
                        Log.i(MAIN_ACTIVITY_KEY, "El nuevo ID del viaje es $nuevoIdCreadoLocal")

                        //y si el viaje ya fue registrado cargara homeTravel
                        replaceFragment(HomeTravelFragment())
                        splashScreen.visibility = View.GONE
                    }
                }.addOnFailureListener {
                    Log.i("ERROR","datos: $it")
                    loadHomeTravelFragment(HomeTravelFragment()) //y si el viaje ya fue registrado cargara homeTravel
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
        Log.i(MAIN_ACTIVITY_KEY, "In method onRestart")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(MAIN_ACTIVITY_KEY, "In method onDestroy")

    }

    // Metodos para llamar los Fragments desde MainActivity


    fun addFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_fragment_container, fragment)
        fragmentTransaction.commit()
    }

    fun addFragmentWithBackStack(fragment: Fragment, TAG: String){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment)
        fragmentTransaction.addToBackStack(TAG)
        fragmentTransaction.commit()
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, fragment)
        fragmentTransaction.commit()
    }



    private fun updateRegistrationTravel(id: String, datein: String) {//Funcion para actualizar registro del viaje con el id que se optendra.
        val registrationFragment =
            TravelRegistrationFragment.newInstance(id, datein) //Aqui se enviara el id del viaje
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, registrationFragment).addToBackStack(null)
        formmu.commit()
        registrationFragment.arguments
    }

    private fun loadTravel(tr: TravelRegistrationFragment) { //Funcion para ingresar un viaje
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(false)
        getSupportActionBar()?.setDisplayShowHomeEnabled(false)
    val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tr).addToBackStack(null)
        formmu.commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Toast.makeText(this,"soy la creacion del menu",Toast.LENGTH_LONG).show()

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menus, menu)
        // val manager=getSystemService() as SearchManager
        var searchItem=menu.findItem(R.id.action_search)
        if (searchItem!=null) {

            var searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Search"
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // logica para filtrar con el recyclerView
                    //adaptador.getFilter().filter(newText)
                    setUpRecyclerView(idTravel,newText)
                    adapterHt!!.startListening()
                    Toast.makeText(this@MainActivity,"$newText",Toast.LENGTH_LONG).show()
                    return true
                }

            })

        }   else    {

            Toast.makeText(this,"no reconoce el searchview",Toast.LENGTH_LONG).show()
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dbAuth = FirebaseAuth.getInstance()

        // Manejar seleccion de Item en Menu (Toolbar)

        return when (item.itemId) {

            // TODO Cambiar los textos del Toast por Strings

            R.id.item_historial -> {
                // Manejar el evento en item "Historial"

                addFragmentWithBackStack(AddRegistroFragment(), ADD_RECORD_FRAGMENT)
                true
            }

            R.id.item_terminos -> {
                // Manejar el evento en item "Terminos y Condiciones"

                Toast.makeText(this, "item terminos y condiciones", Toast.LENGTH_SHORT).show()
                addFragmentWithBackStack(TerminosFragment(), TERMS_AND_CONDITIONS_FRAGMENT)
                true
            }

            R.id.item_generar -> {
                // Manejar el evento en item "Generar Reporte"
                true
            }

            R.id.item_fin_viaje -> {
                // Manejar el evento en item "Finalizar Viaje"

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

    private fun setUpRecyclerView(id:String,consulta:String?){ //metodo para llenar el recyclerview desde firebase id=el id del Record a llenar
        val query: Query = dbCollectionReference!!.document(id)
            .collection("record")
            //.whereEqualTo("recordName",consulta)
            .orderBy("recordName")
            .startAt(consulta).endAt(consulta+"\uf8ff")
        val options: FirestoreRecyclerOptions<Record> = FirestoreRecyclerOptions.Builder<Record>()
            .setQuery(query, Record::class.java)
            .build()
        adapterHt = AdapterHomeTravel(options) //datos reales del adapter
        val recycler = findViewById<RecyclerView>(R.id.recyclerRecord)
        recycler!!.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapterHt
    }

    override fun openRegistrationTravelFragment() {
        addFragmentWithBackStack(TravelRegistrationFragment(), REGISTRATION_TRAVEL_FRAGMENT)
        hideToolBarOnFragmentViewDissapears()
    }

    override fun goBackToHomeTravelFragment(){
        showToolBarOnFragmentViewCreate()
        replaceFragment(HomeTravelFragment())
    }

    override fun openAddRecordFragment(){
         addFragmentWithBackStack(AddRegistroFragment(), ADD_RECORD_FRAGMENT)
    }

    override fun showToolBarOnFragmentViewCreate() {
        var toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.VISIBLE
    }

    override fun hideToolBarOnFragmentViewDissapears() {
        var toolbarMainActivity: View = findViewById(R.id.toolbar)
        toolbarMainActivity.visibility = View.GONE
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
