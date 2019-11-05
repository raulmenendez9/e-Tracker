package com.unicomer.e_tracker_test.travel_registration

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import butterknife.Unbinder
import butterknife.BindView
import butterknife.ButterKnife
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unicomer.e_tracker_test.MainActivity
import com.unicomer.e_tracker_test.R
import kotlinx.android.synthetic.main.fragment_travel_registration.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TravelRegistrationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TravelRegistrationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TravelRegistrationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var mycontext : FragmentActivity?=null
    //Date picker
    var mDateStart: String? = null
    var mDateEnd: String? = null
    var unbinder: Unbinder? = null
    //variables del formulario

    var originCountry: EditText? = null
    var destinyCountry: EditText? = null
    var centerCost: EditText? = null
    var cash: EditText? = null
    var radioYes: RadioButton? = null
    var radioNo: RadioButton?=null
    var datePicker: EditText? = null

    var description: EditText? = null
    var initialTravel: Button? = null
    var closeRegistration:Button? = null
    //accediendo a la instancia de Firestore
    val db = FirebaseFirestore.getInstance()
    var storageRef: StorageReference = FirebaseStorage.getInstance().reference


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originCountry = view.findViewById(R.id.editTextOrigin)
        destinyCountry= view.findViewById(R.id.editTextDestiny)
        centerCost=view.findViewById(R.id.editTextCodProject)
        cash = view.findViewById(R.id.editTextCost)
        radioYes = view!!.findViewById(R.id.radioButtonYes)
        radioNo = view!!.findViewById(R.id.radioButtonNo)
        datePicker = view.findViewById(R.id.editTextDate)

        description = view.findViewById(R.id.editTextMotive)
        initialTravel = view.findViewById(R.id.buttonRegistrations)
        //closeRegistration = view.findViewById(R.id.ButtonCloseRegistration)

        //Radiobuttons
        var radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.radioButtonYes ->{
                    Toast.makeText(activity,radioYes?.text.toString(),Toast.LENGTH_SHORT).show()
                }
                R.id.radioButtonNo ->{
                    Toast.makeText(activity,radioNo?.text.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Termina Radiobuttons

        //Date picker
        ButterKnife.bind(this,view)
        unbinder = ButterKnife.bind(this,view)

       // Objects.requireNonNull((activity as AppCompatActivity).supportActionBar)!!.setDefaultDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.title_date_range_picker_example)

        datePicker!!.setOnClickListener{
            openDateRangePicker()
        }

        //finish Date picker
        initialTravel!!.setOnClickListener{
            registration()
        }

    }

    // TODO: Rename method, update argument and hook method into UI event

    fun registration(){
        //poner asignacion de variable

        Toast.makeText(activity,datePicker?.text.toString(),Toast.LENGTH_SHORT).show()



    }

    fun openDateRangePicker(){
        val pickerFrag = DatePickerFragment()
        pickerFrag.setCallback(object : DatePickerFragment.Callback{
            override fun onCancelled(){
                Toast.makeText(activity,"User cancel",Toast.LENGTH_SHORT).show()
            }
            override fun onDateTimeRecurrenceSet(selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
                                                 recurrenceOption: SublimeRecurrencePicker.RecurrenceOption,
                                                 recurrenceRule: String?) {
                @SuppressLint("SimpleDateFormat")
                val formatDate = SimpleDateFormat("dd-MMM")
                mDateStart = formatDate.format(selectedDate.startDate.time)
                mDateEnd = formatDate.format(selectedDate.endDate.time)

                val date = "${mDateStart +" a "+ mDateEnd}"

                datePicker!!.setText(date)
            }
        })
        // ini configurasi agar library menggunakan method Date Range Picker
        val options = SublimeOptions()
        options.setCanPickDateRange(true)
        options.pickerToShow = SublimeOptions.Picker.DATE_PICKER

        val bundle = Bundle()
        bundle.putParcelable("SUBLIME_OPTIONS", options)
        pickerFrag.arguments = bundle

        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        pickerFrag.show(mycontext!!.supportFragmentManager, "SUBLIME_PICKER")
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }
    override fun onAttach(activity: Activity) {
        mycontext= activity as FragmentActivity
                super.onAttach(activity)
    }


    /*override fun onAttach(context: Context) {
        mycontext = activity as FragmentActivity
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
    }*/
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TravelRegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //Union de 3 ramas
}
