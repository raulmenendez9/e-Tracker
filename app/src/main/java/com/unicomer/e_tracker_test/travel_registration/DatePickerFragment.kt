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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DatePickerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DatePickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DatePickerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DatePickerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

     interface Callback{
        fun onCancelled()
        fun onDateTimeRecurrenceSet(selectedDate: SelectedDate,
                                    hourOfDay: Int, minute: Int,
                                    recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                    recurrenceRule: String?)
    }
}
