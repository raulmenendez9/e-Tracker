package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadHome(HomeFragment())
        //loadRegistrationTravel(TravelRegistrationFragment())
    }

    private fun loadHome(home: HomeFragment) {
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.container_Fragment_Inicio, home).addToBackStack(null)
        formmu.commit()
    }

    private fun loadRegistrationTravel(tr: TravelRegistrationFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.container_Fragment_Inicio, tr).addToBackStack(null)
        formmu.commit()
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
                Toast.makeText(this,"me estoy ejecutando en el item de cerrar",Toast.LENGTH_LONG).show()
                finish()
                true
            }

            else -> {
                Toast.makeText(this,"else ejecutando",Toast.LENGTH_LONG).show()
                super.onOptionsItemSelected(item)

            }
        }
    }

}
