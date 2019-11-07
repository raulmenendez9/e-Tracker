package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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

}
