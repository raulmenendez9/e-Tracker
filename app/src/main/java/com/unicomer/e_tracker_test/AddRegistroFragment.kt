package com.unicomer.e_tracker_test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unicomer.e_tracker_test.travel_registration.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import androidx.databinding.DataBindingUtil
import android.R.attr.data
import androidx.databinding.ViewDataBinding


class AddRegistroFragment : Fragment() {



    private var listener: OnFragmentInteractionListener? = null

    private var radioGroup: RadioGroup? = null
    private var radioButtonFood: RadioButton? = null
    private var radioButtonHotel: RadioButton? = null
    private var radioButtonTransportation: RadioButton? = null
    private var radioButtonOther: RadioButton? = null


    // DatePicker
    private var datePicker: TextView? = null
    // private var finishDate: EditText?=null
    private var unbinder: Unbinder? = null

    // Variables para DatePicker
    var mDateStart: String? = null
    var mDateEnd: String? = null

    // Variable del contexto
    private var mycontext : FragmentActivity? = null

    // FloatingButton
    private var floatingActionButton: FloatingActionButton? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener?.hideToolBarOnFragmentViewDissapears()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
                radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food)
                radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel_gray)
                radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation_gray)
                radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_selected_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonHotel?.id) {
                radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food_gray)
                radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel)
                radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation_gray)
                radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_selected_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonTransportation?.id) {
                radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food_gray)
                radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel_gray)
                radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation)
                radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_selected_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

            } else if (radioButtonSelectedId == radioButtonOther?.id) {
                radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food_gray)
                radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel_gray)
                radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation_gray)
                radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other)

                radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
                radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
                radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_background_gradient)
                radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_selected_background_gradient)
            }

        }

        floatingActionButton = view?.findViewById(R.id.floating_action_button_add_record)
        floatingActionButton?.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener?.hideToolBarOnFragmentViewDissapears()

        datePicker = view.findViewById(R.id.et_date_picker)

        // DatePicker
        ButterKnife.bind(this, view)
        unbinder = ButterKnife.bind(this, view)

        datePicker!!.setOnClickListener{
            openDateRangePicker()
        }


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

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }


    private fun openDateRangePicker(){ //Metodo para abrir el calendario
        val pickerFrag = DatePickerFragment()
        pickerFrag.setCallback(object : DatePickerFragment.Callback{
            override fun onCancelled(){
                Toast.makeText(activity,getString(R.string.User_cancel_datapicker),Toast.LENGTH_SHORT).show()
            }
            override fun onDateTimeRecurrenceSet(selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
                                                 recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                                 recurrenceRule: String?) {
                @SuppressLint("SimpleDateFormat")
                val formatDate = SimpleDateFormat("dd-MM-yyyy")
                mDateStart = formatDate.format(selectedDate.startDate.time)
                // mDateEnd = formatDate.format(selectedDate.endDate.time)

                val initdate = mDateStart
                val finishdate = mDateEnd

                datePicker!!.setText(initdate)
                // finisDate!!.setText(finishdate)
            }
        })

        // inicio de configuracion de library sublime para method Date Range Picker
        val options = SublimeOptions()
        options.setCanPickDateRange(true)
        options.pickerToShow = SublimeOptions.Picker.DATE_PICKER
        val bundle = Bundle()
        bundle.putParcelable("SUBLIME_OPTIONS", options)
        pickerFrag.arguments = bundle
        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        pickerFrag.show(mycontext!!.supportFragmentManager, "SUBLIME_PICKER")
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            sdf.format(Calendar.getInstance().time)
        } catch (e: Exception) {
            e.toString()
        }
    }


    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        mycontext= activity as FragmentActivity
        super.onAttach(activity)
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
        fun showToolBarOnFragmentViewCreate()
        fun hideToolBarOnFragmentViewDissapears()

    }

    companion object {
        @JvmStatic
        fun newInstance() = AddRegistroFragment()
    }
}
