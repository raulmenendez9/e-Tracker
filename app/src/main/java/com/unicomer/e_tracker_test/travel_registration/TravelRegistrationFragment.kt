package com.unicomer.e_tracker_test.travel_registration

import android.annotation.SuppressLint
import android.app.Activity
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.models.Travel
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2= "param2"


class TravelRegistrationFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null


    //variables de los parametros obtenidos del argumento
    private var id: String? = null
    private var datepersist: String? = null
    //variable del contexto
    private var mycontext : FragmentActivity?=null
    //variables para Date picker
    var mDateStart: String? = null
    var mDateEnd: String? = null
    private var unbinder: Unbinder? = null

    //variables del formulario
    private var originCountry:  AutoCompleteTextView? = null
    private var destinyCountry: AutoCompleteTextView? = null
    private var centerCost: EditText? = null
    private var cash: EditText? = null
    //Radiobuttons
    private var radioGroup: RadioGroup? = null
    private var radioYes: RadioButton? = null
    private var radioNo: RadioButton?=null

    private var initialDatePicker: TextView? = null
    private var finishDatePicker: TextView?=null
    private var spinner : Spinner? = null
    private var aproved : MutableList<String> = mutableListOf()

    private var description: EditText? = null
    private var initialTravel: Button? = null
    private var floatingActionButton: FloatingActionButton? = null
    //accediendo a la instancia de Firestore
    private val user = FirebaseAuth.getInstance().currentUser
    private var emailUser: String?=null
    private val db = FirebaseFirestore.getInstance()
    private val aprovedRef = FirebaseFirestore.getInstance()
    private val travelAprovRef = aprovedRef.collection("travel_approvers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            datepersist= it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_travel_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ocultar el toolbar
        listener?.hideToolBarOnFragmentViewDissapears()


        originCountry = view.findViewById(R.id.editTextOrigin)
        destinyCountry= view.findViewById(R.id.editTextDestiny)
        centerCost=view.findViewById(R.id.editTextCodProject)
        cash = view.findViewById(R.id.editTextCost)
        radioYes = view.findViewById(R.id.radioButtonYes)
        radioNo = view.findViewById(R.id.radioButtonNo)
        initialDatePicker = view.findViewById(R.id.textView_pick_initial_date)
        finishDatePicker = view.findViewById(R.id.textView_pick_final_date)
        spinner = view.findViewById(R.id.spinnerAproved)
        description = view.findViewById(R.id.editTextMotive)
        initialTravel = view.findViewById(R.id.buttonRegistrations)
        floatingActionButton = view.findViewById(R.id.ButtonCloseRegistration)
        radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        //AutocompleteTextview
        val countries = resources.getStringArray(R.array.coutries_array)
        val contriAdapter= ArrayAdapter(activity!!,android.R.layout.simple_list_item_1,countries)
        originCountry!!.setAdapter(contriAdapter)
        destinyCountry!!.setAdapter(contriAdapter)
        //Finish autocomplate

        //Date picker
        ButterKnife.bind(this,view)
        unbinder = ButterKnife.bind(this,view)

        initialDatePicker!!.setOnClickListener{
            openDateRangePicker()
        }

        finishDatePicker!!.setOnClickListener{
            openDateRangePicker()
        }
        //finish Date picker

        //condicional para entrar a crear un nuevo viaje o para actualizar el viaje
        if (id == null) {
            //Spinner
            val adapt = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, aproved)
            adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner!!.adapter = adapt
            travelAprovRef.get().addOnCompleteListener{
                if (it.isSuccessful){
                    for (document: QueryDocumentSnapshot in it.result!!){
                        val aprovedTravel = document.getString("Name")
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
            initialTravel!!.text= getString(R.string.update_travel) //Cambio de texto del boton
             getTravels(id!!) //Obtencion de la data
            initialTravel!!.setOnClickListener{
        updateTravel(id!!,datepersist!!)} //Actualizacion de la data
        }
        floatingActionButton!!.setOnClickListener{
            activity!!.supportFragmentManager.popBackStack()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

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
                mDateEnd = formatDate.format(selectedDate.endDate.time)

                val initdate = mDateStart
                val finishdate = mDateEnd

                initialDatePicker!!.setText(initdate)
                finishDatePicker!!.setText(finishdate)
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

    private fun registration() {//Metodo para registrar viaje nuevo
        if (originCountry!!.text.toString().isEmpty() || destinyCountry!!.text.toString().isEmpty()
            || centerCost!!.text.toString().isEmpty() || cash!!.text.toString().isEmpty()
            || (radioGroup!!.checkedRadioButtonId == -1) || initialDatePicker!!.text.toString().isEmpty()
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
            val origCountry = originCountry!!.text.toString()
            val destCountry = destinyCountry!!.text.toString()
            val cenCost = centerCost!!.text.toString()
            val cassh = cash!!.text.toString()
            val datePick = initialDatePicker!!.text.toString()
            val finishtravel = finishDatePicker!!.text.toString()
            val descp = description!!.text.toString()
            val balance = cassh
            val aproved = spinner!!.selectedItem.toString()
            emailUser=user!!.email
            val email = emailUser
            val date = getDateTime()
            val update = null
            val active = true
            val settled = false

            //RadioButton
            var refund:String? = null
            val selectedId : Int = radioGroup!!.checkedRadioButtonId

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
                cassh,
                email,
                refund,
                datePick,
                finishtravel,
                date,
                update,
                aproved,
                descp,
                balance,
                active,
                settled)

                db.collection("e-Tracker")
                    .add(travel)
                    .addOnSuccessListener {
                        Log.d("Enviodata", "$travel")
                        Toast.makeText(activity, getString(R.string.register_complete), Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e -> Log.w("Error", "$e") }
        }
    }

//obtencion de la data del viaje ingresado
    private fun getTravels(id: String) {
        var viaje: MutableList<Travel>
        val docRef = db.collection("e-Tracker")
        val query: Query = docRef.whereEqualTo(FieldPath.documentId(), id)
            query.get().addOnSuccessListener {documentSnapshot->
                 viaje = documentSnapshot.toObjects(Travel::class.java)
                originCountry!!.setText(viaje[0].originCountry)
                destinyCountry!!.setText(viaje[0].destinyCountry)
                centerCost!!.setText(viaje[0].centerCost)
                cash!!.setText(viaje[0].cash)
                //optencion del valor del radioButton
                if (viaje[0].refund == "Si"){
                    radioYes= radioGroup!!.getChildAt(0) as RadioButton?
                    radioGroup!!.check(radioYes!!.id)
                }else if (viaje[0].refund=="No"){
                    radioNo= radioGroup!!.getChildAt(1) as RadioButton?
                    radioGroup!!.check(radioNo!!.id)
                }
                //finalizacion del radioButton
                initialDatePicker!!.setText(viaje[0].initialDate)
                finishDatePicker!!.setText(viaje[0].finishDate)
                description!!.setText(viaje[0].description)
                //Spinner
                var conteo: Int
                val adapt = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, aproved)
                adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner!!.adapter = adapt
                travelAprovRef.get().addOnCompleteListener{
                    if (it.isSuccessful){
                        for (document: QueryDocumentSnapshot in it.result!!){
                            val aprovedTravel = document.getString("Name")
                                aproved.add(aprovedTravel!!)
                        }
                        conteo = aproved.count()-1
                        for (i in  0..conteo){
                            if (spinner!!.getItemAtPosition(i) == viaje[0].aproved)
                                spinner!!.setSelection(i)
                        }
                        adapt.notifyDataSetChanged()
                    }else{ Log.d("No_Success", "datos no funcionando")}
                }
                //Finish Spinner
                //Finaliza de llenar los datos en el formulario
            }
    }

    private fun updateTravel(id: String, persist: String){//Creacion del Actualizar Datos del viaje
        if (originCountry!!.text.toString().isEmpty() || destinyCountry!!.text.toString().isEmpty()
            || centerCost!!.text.toString().isEmpty() || cash!!.text.toString().isEmpty()
            || (radioGroup!!.checkedRadioButtonId == -1) || initialDatePicker!!.text.toString().isEmpty()
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
            val origCountry = originCountry!!.text.toString()
            val destCountry = destinyCountry!!.text.toString()
            val cenCost = centerCost!!.text.toString()
            val cassh = cash!!.text.toString()
            val datePick = initialDatePicker!!.text.toString()
            val finishtravel = finishDatePicker!!.text.toString()
            val descp = description!!.text.toString()
            val balance = cassh
            val aproved = spinner!!.selectedItem.toString()
            emailUser=user!!.email
            val email = emailUser
            val date = persist
            val update = getDateTime()
            val active = true
            val settled = false

            //RadioButton
            var refund:String? = null
            val selectedId : Int = radioGroup!!.checkedRadioButtonId

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
                balance,active, settled
            )
            db.collection("e-Tracker").document(id)
                .set(travel) //Realiza el seteo de la data en firebase
                .addOnSuccessListener {
                    Log.d("Enviodata", "$travel")
                    Toast.makeText(activity, getString(R.string.register_update), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e -> Log.w("Error", "$e") }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        unbinder!!.unbind()
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        mycontext= activity as FragmentActivity
                super.onAttach(activity)
    }

    interface OnFragmentInteractionListener {
        fun hideToolBarOnFragmentViewDissapears()
    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(id: String, datein: String) =
            TravelRegistrationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, id)
                    putString(ARG_PARAM2,datein)
                }
            }
    }

}
