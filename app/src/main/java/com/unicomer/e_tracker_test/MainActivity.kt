package com.unicomer.e_tracker_test

import android.app.SearchManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat

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

        val toolbar = this.findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        loadHome(HomeFragment())
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
       // Toast.makeText(this,"soy la creacion del menu",Toast.LENGTH_LONG).show()
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menus, menu)
//        val manager=getSystemService() as SearchManager
        var searchItem=menu.findItem(R.id.action_search)
        if (searchItem!=null) {

            var searchView = searchItem.actionView as SearchView
            searchView.queryHint = "Search"
            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Toast.makeText(this@MainActivity,"$newText",Toast.LENGTH_LONG).show()
                    return true
                }

            })
        }else{
            Toast.makeText(this,"no reconoce el searchview",Toast.LENGTH_LONG).show()
        }


        return super.onCreateOptionsMenu(menu)
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
                true
            }

            else -> {
                Toast.makeText(this,"ningun item",Toast.LENGTH_LONG).show()
                super.onOptionsItemSelected(item)}
        }
    }

}
//sethasoptionmenu en los fragmentos
// habilita el toolbart en los demas fragmentos

