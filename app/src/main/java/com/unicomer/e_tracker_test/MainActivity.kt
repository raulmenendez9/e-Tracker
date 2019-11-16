package com.unicomer.e_tracker_test

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.unicomer.e_tracker_test.Constants.MAIN_ACTIVITY_KEY
import com.unicomer.e_tracker_test.Constants.REGISTRATION_TRAVEL_FRAGMENT
import com.unicomer.e_tracker_test.Constants.TERMS_AND_CONDITIONS_FRAGMENT

import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment

class MainActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener, AddRegistroFragment.OnFragmentInteractionListener, TerminosFragment.OnFragmentInteractionListener {

    // Declaring FirebaseAuth components
    private var dbAuth: FirebaseAuth? = null
    // End of Declaring FirebaseAuth components


    val idd: String = ""
    val dateinit: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbAuth = FirebaseAuth.getInstance()
        val user = dbAuth!!.currentUser


        // Si usuario SI ES null entonces MainActivity NO se ejecuta y pasa directamente a LoginActivity
        if (user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        // Cargar MainFragment para Inicio de Navegacion en UI
        loadHomeFragment(HomeFragment())
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //para el back button del toolbar

    }




    private fun envio() {
        var barra: View = findViewById(R.id.toolbar)
        barra.visibility = View.GONE
        //updateRegistrationTravel(idd,dateinit)//llamado al metodo para actualizar registro del viaje
        loadTravel(TravelRegistrationFragment())//LLamado al metodo para registrar viaje
    }


    private fun loadHomeFragment(homeFragment: HomeFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_fragment_container, homeFragment)
        fragmentTransaction.commit()
    }

    private fun loadRegistrationTravelFragment(travelRegistrationFragment: TravelRegistrationFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, travelRegistrationFragment)
        fragmentTransaction.addToBackStack(REGISTRATION_TRAVEL_FRAGMENT)
        fragmentTransaction.commit()
    }

    private fun loadTermsAndConditionsFragment(termsAndConditionsFragment: TerminosFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, termsAndConditionsFragment)
        fragmentTransaction.addToBackStack(TERMS_AND_CONDITIONS_FRAGMENT)
        fragmentTransaction.commit()
    }

    private fun updateRegistrationTravel(
        id: String,
        datein: String
    ) {//Funcion para actualizar registro del viaje con el id que se optendra.
        val registrationFragment =
            TravelRegistrationFragment.newInstance(id, datein) //Aqui se enviara el id del viaje
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, registrationFragment).addToBackStack(null)
        formmu.commit()
        registrationFragment.arguments
    }

    private fun loadTravel(tr: TravelRegistrationFragment) { //Funcion para ingresar un viaje
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tr).addToBackStack(null)
        formmu.commit()
    }

    private fun thisIsATestFragment(addRecordFragment: AddRegistroFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment_container, addRecordFragment)
        fragmentTransaction.addToBackStack(MAIN_ACTIVITY_KEY)
        fragmentTransaction.commit()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        dbAuth = FirebaseAuth.getInstance()

        // Manejar seleccion de Item en Menu (Toolbar)
        return when (item.itemId) {

            // TODO Cambiar los textos del Toast por Strings

            R.id.item_historial -> {
                // Manejar el evento en item "Historial"

                thisIsATestFragment(AddRegistroFragment())
                true
            }

            R.id.item_terminos -> {
                // Manejar el evento en item "Terminos y Condiciones"

                Toast.makeText(this, "item terminos y condiciones", Toast.LENGTH_SHORT).show()
                loadTermsAndConditionsFragment(TerminosFragment())
                true
            }

            R.id.item_generar -> {
                // Manejar el evento en item "Generar Reporte"

                Toast.makeText(this, "item generar", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.item_fin_viaje -> {
                // Manejar el evento en item "Finalizar Viaje"

                Toast.makeText(this, "item fin viaje", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.item_cerrarapp -> {
                // Manejar el evento en item "Cerrar sesion"

                Toast.makeText(this, "La sesion ha finalizado", Toast.LENGTH_SHORT).show()
                dbAuth?.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            else -> {
                Toast.makeText(this, "ningun item", Toast.LENGTH_SHORT).show()
                super.onOptionsItemSelected(item)
            }
        }
    }



    override fun openRegistrationTravelFragment() {
        hideToolBarOnFragmentViewDissapears()
        loadRegistrationTravelFragment(TravelRegistrationFragment())
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
