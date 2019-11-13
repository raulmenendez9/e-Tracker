package com.unicomer.e_tracker_test.travel_registration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
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
import butterknife.Unbinder
import butterknife.ButterKnife
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Travel
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
    private var id: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
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
    var finisDate: EditText?=null
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
    var storageRef: StorageReference = FirebaseStorage.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
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
        radioYes = view.findViewById(R.id.radioButtonYes)
        radioNo = view.findViewById(R.id.radioButtonNo)
        datePicker = view.findViewById(R.id.editTextDate)
        finisDate = view.findViewById(R.id.finishDate)
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

        if (id == null) {
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
            initialTravel!!.setOnClickListener {
                registration()
            }
        }else{
            getTravels(id!!)
            initialTravel!!.setOnClickListener{
        updateTravel(id!!)}
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
                val formatDate = SimpleDateFormat("dd-MM-yyyy")
                mDateStart = formatDate.format(selectedDate.startDate.time)
                mDateEnd = formatDate.format(selectedDate.endDate.time)

                val initdate = mDateStart
                val finishdate = mDateEnd

                datePicker!!.setText(initdate)
                finisDate!!.setText(finishdate)
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
            var finishtravel = finisDate!!.text.toString()
            var descp = description!!.text.toString()
            var balance = cassh
            var aproved = spinner!!.selectedItem.toString()
                emailUser=user!!.email
            var email = emailUser
            var date = getDateTime()
            var update = null
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
                cassh, email, refund,
                datePick,finishtravel,date,update,aproved,
                descp,
                balance
            )
                db.collection("e-Tracker")
                    .add(travel)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Enviodata", "$travel")
                        Toast.makeText(activity, "Registro completado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e -> Log.w("Error", "$e") }
        }
    }

    //Creacion del Actualizar Datos del viaje

    fun getTravels(id: String) {
        var pos =0
        var i =0
        var viaje: MutableList<Travel> = mutableListOf()
            val docRef = db.collection("e-Tracker")
            val query:Query = docRef.whereEqualTo(FieldPath.documentId(), id)
            query.get().addOnSuccessListener {documentSnapshot->
                 viaje = documentSnapshot.toObjects(Travel::class.java)
                originCountry!!.setText(viaje[0].originCountry)
                destinyCountry!!.setText(viaje[0].destinyCountry)
                centerCost!!.setText(viaje[0].centerCost)
                cash!!.setText(viaje[0].cash)
                if (viaje[0].refund == "Si"){
                    radioYes!!.id
                }else if (viaje[0].refund=="No"){radioNo!!.id}
                datePicker!!.setText(viaje[0].initialDate)
                finisDate!!.setText(viaje[0].finishDate)
                description!!.setText(viaje[0].description)
                //Spinner
                val adapt = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, aproved)
                adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner!!.adapter = adapt
                travelAprovRef.get().addOnCompleteListener{
                    if (it.isSuccessful){
                        for (document: QueryDocumentSnapshot in it.result!!){
                            var aprovedTravel = document.getString("Name")
                            aproved.add(aprovedTravel!!)
                            var conteo = spinner!!.count

                            for (i in  0..conteo){
                                if (spinner!!.getItemAtPosition(i).toString().equals(viaje[0].aproved))
                                    spinner!!.setSelection(i)
                            }
                            Log.d("Success", "$aproved")
                        }
                        adapt.notifyDataSetChanged()
                    }
                    Log.d("No_Success", "datos no funcionando")
                }
                //Finish Spinner

                //Finaliza de llenar los datos en el formulario
            }

    }

    fun updateTravel(id: String){
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
            var finishtravel = finisDate!!.text.toString()
            var descp = description!!.text.toString()
            var balance = cassh
            var aproved = spinner!!.selectedItem.toString()
            emailUser=user!!.email
            var email = emailUser
            var date = null
            var update = getDateTime()
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
                cassh, email, refund,
                datePick,finishtravel,date,update,aproved,
                descp,
                balance
            )
            db.collection("e-Tracker").document(id)
                .set(travel)
                .addOnSuccessListener { documentReference ->
                    Log.d("Enviodata", "$travel")
                    Toast.makeText(activity, "Registro completado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> Log.w("Error", "$e") }
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(): String? {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        } catch (e: Exception) {
            return e.toString()
        }
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: String) =
            TravelRegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                }
            }
    }


}
