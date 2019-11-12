package com.unicomer.e_tracker_test.travel_registration

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Travel
import java.text.SimpleDateFormat


class TravelUpdateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var mycontext : FragmentActivity?=null
    //Date picker
    var mDateStart: String? = null
    var mDateEnd: String? = null
    var unbinder: Unbinder? = null
    //variables del formulario

    var originCountry:  AutoCompleteTextView? = null
    var destinyCountry: AutoCompleteTextView? = null
    var centerCost: EditText? = null
    var cash: EditText? = null
    //Radiobuttons
    var radioGroup: RadioGroup? = null
    var radioYes: RadioButton? = null
    var radioNo: RadioButton?=null

    var datePicker: EditText? = null
    var spinner : Spinner? = null
    var textSpinner: String? = null
    var aproved : MutableList<String> = mutableListOf()

    var description: EditText? = null
    var initialTravel: Button? = null
    var closeRegistration: FloatingActionButton? = null
    //accediendo a la instancia de Firestore
    val user = FirebaseAuth.getInstance().currentUser
    var emailUser: String?=null
    val db = FirebaseFirestore.getInstance()
    val aprovedRef = FirebaseFirestore.getInstance()
    val travelAprovRef = aprovedRef.collection("travel_approvers")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel_registration, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        originCountry = view.findViewById(R.id.editTextOrigin)
        destinyCountry= view.findViewById(R.id.editTextDestiny)
        centerCost=view.findViewById(R.id.editTextCodProject)
        cash = view.findViewById(R.id.editTextCost)
        radioYes = view.findViewById(R.id.radioButtonYes)
        radioNo = view.findViewById(R.id.radioButtonNo)
        datePicker = view.findViewById(R.id.editTextDate)
        spinner = view.findViewById(R.id.spinnerAproved)
        description = view.findViewById(R.id.editTextMotive)
        initialTravel = view.findViewById(R.id.buttonRegistrations)
        closeRegistration = view.findViewById(R.id.ButtonCloseRegistration)


        //AutocompleteTextview
        val countries = resources.getStringArray(R.array.coutries_array)
        val contriAdapter= ArrayAdapter(activity!!,android.R.layout.simple_list_item_1,countries)
        originCountry!!.setAdapter(contriAdapter)
        destinyCountry!!.setAdapter(contriAdapter)

        //Finish autocomplate
        radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)


        //Date picker
        ButterKnife.bind(this,view)
        unbinder = ButterKnife.bind(this,view)

        datePicker!!.setOnClickListener{
            openDateRangePicker()
        }

        //finish Date picker

        //Spinner
        val adapt = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, aproved)
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapt
        travelAprovRef.get().addOnCompleteListener{
            if (it.isSuccessful){
                for (document: QueryDocumentSnapshot in it.result!!){
                    var aprovedTravel = document.getString("Name")
                    aproved.add(aprovedTravel!!)

                    Log.d("Success", "$aproved")
                }
                adapt.notifyDataSetChanged()
            }
            Log.d("No_Success", "datos no funcionando")
        }
        //Finish Spinner
        initialTravel!!.setOnClickListener{
            registration()
        }

        closeRegistration!!.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

    fun registration(){
        edittextValidations()
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

                val date = "$mDateStart a $mDateEnd"

                datePicker!!.setText(date)
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

    fun edittextValidations() {
        if (originCountry!!.text.toString().isEmpty() || destinyCountry!!.text.toString().isEmpty()
            || centerCost!!.text.toString().isEmpty() || cash!!.text.toString().isEmpty()
            || (radioGroup!!.checkedRadioButtonId == -1) || datePicker!!.text.toString().isEmpty()
            || description!!.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, activity!!.getString(R.string.error_hint), Toast.LENGTH_SHORT)
                .show()
        }
        if (centerCost!!.length() < 7) {
            Toast.makeText(
                activity,
                activity!!.getString(R.string.error_center_cost),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            var origCountry = originCountry!!.text.toString()
            var destCountry = destinyCountry!!.text.toString()
            var cenCost = centerCost!!.text.toString()
            var cassh = cash!!.text.toString()
            var datePick = datePicker!!.text.toString()
            var descp = description!!.text.toString()
            var balance = cassh
            var aproved = spinner!!.selectedItem.toString()
            emailUser=user!!.email

            //RadioButton
            var refund:String? = null
            var selectedId : Int = radioGroup!!.checkedRadioButtonId

            if (selectedId == radioYes!!.id){
                refund= radioYes?.text.toString()
            }else if (selectedId == radioNo!!.id){
                refund= radioNo?.text.toString()
            }
            //Termina Radiobuttons
            val travel = Travel(
                origCountry,
                destCountry,
                cenCost,
                cassh, refund,
                datePick,aproved,
                descp,
                balance
            )
            emailUser?.let {
                db.collection("e-Tracker").document(it).collection("Travels")
                    .add(travel)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Enviodata", "$travel")
                        Toast.makeText(activity, "Registro completado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e -> Log.w("Error", "$e") }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }
}
