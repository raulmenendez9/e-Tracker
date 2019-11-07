package com.unicomer.e_tracker_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadRegistrationTravel(TravelRegistrationFragment())

    }

    private fun loadRegistrationTravel(tr: TravelRegistrationFragment){
        val formmu = supportFragmentManager.beginTransaction()
        formmu.replace(R.id.main_fragment_container, tr).addToBackStack(null)
        formmu.commit()
    }

}
