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

import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment

class MainActivity : AppCompatActivity(),HomeFragment.OnFragmentInteractionListener, AddRegistroFragment.OnFragmentInteractionListener {

    // Declaring FirebaseAuth components
    private var dbAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        loadHome(HomeFragment())
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //para el back button del toolbar
        //loadRegistrationTravel(TravelRegistrationFragment())





    }

    private fun loadHome(home: HomeFragment) {
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, home)
        formmu.commit()
    }

    private fun loadRegistrationTravel(tr: TravelRegistrationFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tr)
        formmu.addToBackStack(null)
        formmu.commit()
    }
    private fun loadTerms(tyc:TerminosFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tyc).addToBackStack(null)
        formmu.commit()
    }


    private fun thisIsATestFragment(addRecordFragment: AddRegistroFragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment_container, addRecordFragment)
        transaction.addToBackStack(MAIN_ACTIVITY_KEY)
        transaction.commit()
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
        // Handle item selection
        return when (item.itemId) {

            R.id.item_cerrarapp -> {
                Toast.makeText(this,"La sesion ha finalizado",Toast.LENGTH_LONG).show()
                dbAuth?.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

                true
            }

            R.id.item_fin_viaje -> {
                Toast.makeText(this,"item fin viaje",Toast.LENGTH_LONG).show()
                true
            }

            R.id.item_historial -> {
                // Toast.makeText(this,"item historial",Toast.LENGTH_LONG).show()
                // This is just a test
                // TODO Borrar esto

                thisIsATestFragment(AddRegistroFragment())


                true
            }

            R.id.item_generar -> {
                Toast.makeText(this,"item generar",Toast.LENGTH_LONG).show()
                true
            }

            R.id.item_terminos -> {
                Toast.makeText(this,"item terminos y condiciones",Toast.LENGTH_LONG).show()
                loadTerms(TerminosFragment())
                true
            }


            else -> {
                Toast.makeText(this,"ningun item",Toast.LENGTH_LONG).show()
                super.onOptionsItemSelected(item)}
        }
    }


    override fun envio() {
        var barra: View = findViewById(R.id.toolbar)
        barra.visibility = View.GONE
        loadRegistrationTravel(TravelRegistrationFragment())
    }

    override fun OnAttachHomeFragment(){
        var toolbarMenu : View = findViewById(R.id.toolbar)
        toolbarMenu.visibility = View.VISIBLE
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
