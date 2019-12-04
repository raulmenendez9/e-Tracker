package com.unicomer.e_tracker_test.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.unicomer.e_tracker_test.R


// Esta clase se utiliza para abrir Fragments

class CallFragment{

    /**
     *  @sample CallFragment().addFragment(this.supportFragmentManager, TravelRegistrationFragment(), true, true, 1)
     *
     *  @param1 FragmentManager from Activity (this@Activity.supportFragmentManager)
     *  @param2 Fragment needs to be opened
     *  @param3 If the fragment nedeeds to be replaced or added
     *  @param4 If the fragment needs to be added to backStack
     *  @param5 If animations are needed
     */

    fun addFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        replace: Boolean,
        addToBackStack: Boolean,
        addAnimation: Int
    ){
        val fragmentTransaction = fragmentManager.beginTransaction()

        when (addAnimation){

            0   ->  {fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)}
            1   ->  {fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)}
            2   ->  {fragmentTransaction.setCustomAnimations(R.anim.move_up, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)}
            3   ->  {fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)}

            // Animacion Default
            else -> {fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)}

        }
        if (replace) fragmentTransaction.replace(R.id.main_fragment_container, fragment, fragment.javaClass.name)
        else fragmentTransaction.add(R.id.main_fragment_container, fragment, fragment.javaClass.name)

        if (addToBackStack) fragmentTransaction.addToBackStack(fragment.javaClass.name)
        fragmentTransaction.commit()
    }

}

//0=fade in, fade out
//1= enter right, exit right
//2= move up, fade_out