package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.unicomer.e_tracker_test.travel_registration.TravelRegistrationFragment

class MainActivity : AppCompatActivity(),HomeFragment.OnFragmentInteractionListener {
    override fun envio() {
        var barra: View = findViewById(R.id.toolbar)
        barra.visibility = View.GONE
        loadRegistrationTravel(TravelRegistrationFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //loadHome(HomeFragment())
        loadHomeTravel(HomeTravelFragment())
        //loadRegistrationTravel(TravelRegistrationFragment())
    }

    private fun loadHome(home: HomeFragment) {
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, home).addToBackStack(null)
        formmu.commit()
    }

    private fun loadRegistrationTravel(tr: TravelRegistrationFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tr).addToBackStack(null)
        formmu.commit()
    }
    private fun loadTerms(tyc:TerminosFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tyc).addToBackStack(null)
        formmu.commit()
    }
    private fun loadHomeTravel(ht:HomeTravelFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, ht).addToBackStack(null)
        formmu.commit()
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
        // Handle item selection
        return when (item.itemId) {
            R.id.item_cerrarapp -> {
                Toast.makeText(this,"item cerrar app",Toast.LENGTH_LONG).show()
                finish()
                true
            }
            R.id.item_fin_viaje -> {
                Toast.makeText(this,"item fin viaje",Toast.LENGTH_LONG).show()
                true
            }
            R.id.item_historial -> {
                Toast.makeText(this,"item historial",Toast.LENGTH_LONG).show()
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

}
