package com.unicomer.e_tracker_test.travel_registration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.appeaser.sublimepickerlibrary.SublimePicker
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.unicomer.e_tracker_test.R
import java.text.DateFormat
import java.util.*


class DatePickerFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mDateFormatter: DateFormat?=null
    var mTimeFormatter: DateFormat?=null
     internal var mSublimePicker: SublimePicker?=null
    var mCallback: Callback?=null
    internal var mListener : SublimeListenerAdapter = object : SublimeListenerAdapter(){

        @Override
        override fun onCancelled(){
            if (mCallback != null){
                mCallback!!.onCancelled()
            }
            dismiss()
        }

        override fun onDateTimeRecurrenceSet(
            sublimeMaterialPicker: SublimePicker,
            selectedDate: SelectedDate,
            hourOfDay: Int,
            minute: Int,
            recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
            recurrenceRule: String?
        ) {
            if (mCallback != null){
                mCallback!!.onDateTimeRecurrenceSet(selectedDate,hourOfDay,
                    minute, recurrenceOption, recurrenceRule)
            }
            dismiss()
        }
    }

    fun DatePickerFragment(){
        mDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        mTimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        mTimeFormatter!!.timeZone
        //Probar asi si no poner (TimeZone.getTimeZone("GMT+0")) pero da error :'v
    }

    fun setCallback(callback: Callback){mCallback = callback}

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSublimePicker = activity?.layoutInflater!!.inflate(R.layout.sublime_picker, container) as SublimePicker

        // Retrieve SublimeOptions
        val arguments = arguments
        var options: SublimeOptions? = null

        // Options can be null, in which case, default
        // options are used.
        if (arguments != null) {
            options = arguments!!.getParcelable("SUBLIME_OPTIONS")
        }

        mSublimePicker!!.initializePicker(options, mListener)
        return mSublimePicker
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = DatePickerFragment()
    }

     interface Callback{
        fun onCancelled()
        fun onDateTimeRecurrenceSet(selectedDate: SelectedDate,
                                    hourOfDay: Int, minute: Int,
                                    recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                    recurrenceRule: String?)
    }
}
