package com.unicomer.e_tracker_test.Classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.unicomer.e_tracker_test.R


// Esta clase se utiliza para abrir Fragments

class CallFragment{

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