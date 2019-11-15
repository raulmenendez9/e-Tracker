package com.unicomer.e_tracker_test

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.unicomer.e_tracker_test.Constants.ADD_RECORD_FRAGMENT
import android.widget.Toast




class AddRegistroFragment : Fragment() {



    private var listener: OnFragmentInteractionListener? = null

    private var radioGroup: RadioGroup? = null
    private var radioButtonFood: RadioButton? = null
    private var radioButtonHotel: RadioButton? = null
    private var radioButtonTransportation: RadioButton? = null
    private var radioButtonOther: RadioButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_registro, container, false)



        radioButtonFood = view?.findViewById(R.id.radioButton_food)
        radioButtonTransportation = view?.findViewById(R.id.radioButton_transportation)
        radioButtonHotel = view?.findViewById(R.id.radioButton_hotel)
        radioButtonOther = view?.findViewById(R.id.radioButton_other)

        radioGroup = view?.findViewById(R.id.radioGroup_Category)


        radioGroup?.setOnCheckedChangeListener {group, checkedId ->
            val radioButton = view?.findViewById<RadioButton>(checkedId)

            var radioButtonSelectedId: Int = radioGroup!!.checkedRadioButtonId


            if (radioButtonSelectedId == radioButtonFood?.id){
                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_selected_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_car_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonHotel?.id) {

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_selected_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_car_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonTransportation?.id) {

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_car_selected_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonOther?.id) {
                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_car_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_selected_background_gradient)
            }

        }

        return view
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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

    private fun radioButtonOnClickBackground(){


    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddRegistroFragment()
    }
}
