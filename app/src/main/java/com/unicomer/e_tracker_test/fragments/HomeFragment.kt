package com.unicomer.e_tracker_test.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unicomer.e_tracker_test.R


class HomeFragment : Fragment() {


    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=inflater.inflate(R.layout.fragment_home, container, false)

        val floatingButtonHomeFragment: View = view.findViewById(R.id.floatingActionButton_addviaje)

        listener?.showToolBarOnFragmentViewCreate()

        floatingButtonHomeFragment.setOnClickListener {
            listener?.openRegistrationTravelFragment()
        }

        return view
    }



    override fun onAttach(context: Context) {

        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {

        fun openRegistrationTravelFragment()
        fun showToolBarOnFragmentViewCreate()
        fun hideToolBarOnFragmentViewDissapears()

    }



    companion object {


        @JvmStatic
        fun newInstance() = HomeFragment()
            }
    }

