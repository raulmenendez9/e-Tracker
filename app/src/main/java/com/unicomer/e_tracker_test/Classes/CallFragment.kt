package com.unicomer.e_tracker_test.Classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.unicomer.e_tracker_test.R


// Esta clase se utiliza para abrir Fragments

class CallFragment{

    /**
     *  @sample CallFragment().addFragment(this.supportFragmentManager, TravelRegistrationFragment(), true, true, true)
     *
     *  @param1 FragmentManager from Activity (this@Activity.supportFragmentManager)
     *  @param2 Fragment needs to be opened
     *  @param3 If the fragment needs to be replaced or added
     *  @param4 If the fragment needs to be added to backStack
     *  @param5 If animations are needed
     */

    fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        replace: Boolean,
        addToBackStack: Boolean,
        addAnimation: Boolean
    ){
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (addAnimation) fragmentTransaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        )
        if (replace) fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.javaClass.name)
        else fragmentTransaction.add(R.id.main_fragment_container, fragment, fragment.javaClass.name)

        if (addToBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.name)
        fragmentTransaction.commit()
    }

}