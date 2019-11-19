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
import android.util.Log
import com.google.firebase.firestore.*
import com.unicomer.e_tracker_test.Constants.*
import com.unicomer.e_tracker_test.Models.Record
import com.unicomer.e_tracker_test.Models.Travel


class AddRegistroFragment : Fragment() {


    private var listener: OnFragmentInteractionListener? = null

    // Elementos de UI
    private var editTextName: EditText? = null
    private var fecha: TextView? = null
    private var monto: EditText? = null
    private var editTextDescripcion: EditText? = null

    // Contenedor RadioGroup
    private var radioGroup: RadioGroup? = null

    // UI de RadioButton
    private var radioButtonFood: RadioButton? = null
    private var radioButtonHotel: RadioButton? = null
    private var radioButtonTransportation: RadioButton? = null
    private var radioButtonOther: RadioButton? = null

    // Variables para ID de cada RadioButton
    private val buttonFoodId: Int? = 0
    private val buttonHotelId: Int? = 1
    private val buttonTransportationId: Int? = 2
    private val buttonOtherId: Int? = 3

    // Boton Tomar Foto

    private var buttonTakePhoto: Button? = null

    // Boton Agregar Registro

    private var buttonAddRecord: Button? = null

    // FloatingButton
    private var floatingActionButton: FloatingActionButton? = null

    // DatePicker
    private var datePicker: TextView? = null
    // private var finishDate: EditText?=null
    private var unbinder: Unbinder? = null

    // Variables para DatePicker
    var mDateStart: String? = null
    var mDateEnd: String? = null

    // Variable del contexto
    private var mycontext : FragmentActivity? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar el Toolbar al inicio
        listener?.hideToolBarOnFragmentViewDissapears()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_registro, container, false)

        // RadioGroup contenedor de RadioButton
        radioGroup = view?.findViewById(R.id.radioGroup_Category)

        // Botones del Radio Button
        radioButtonFood = view?.findViewById(R.id.radioButton_food)
        radioButtonFood?.id = buttonFoodId!!

        radioButtonHotel = view?.findViewById(R.id.radioButton_hotel)
        radioButtonHotel?.id = buttonHotelId!!

        radioButtonTransportation = view?.findViewById(R.id.radioButton_transportation)
        radioButtonTransportation?.id = buttonTransportationId!!

        radioButtonOther = view?.findViewById(R.id.radioButton_other)
        radioButtonOther?.id = buttonOtherId!!

        // Detectar el ID del RadioButton

        radioGroup?.setOnCheckedChangeListener {group, checkedId ->

            var radioButtonSelectedId: Int = radioGroup!!.checkedRadioButtonId
            radioButtonSelection(radioButtonSelectedId)
            Log.i(ADD_RECORD_FRAGMENT, "radioButtonSelectionID es ${radioButtonSelectedId}")

        }

        // Listener de los Botones

        buttonTakePhoto = view?.findViewById(R.id.btn_tomar_foto)

        buttonAddRecord = view?.findViewById(R.id.btn_agregar_registro)
        buttonAddRecord?.setOnClickListener {
            createRecordInFirestore()
        }



        //Manipular el FloatinActionButton
        floatingActionButton = view?.findViewById(R.id.floating_action_button_add_record)
        floatingActionButton?.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener?.hideToolBarOnFragmentViewDissapears()

        datePicker = view.findViewById(R.id.textview_record_date_selection)

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


    private fun createRecordInFirestore(){

        // Init UI

        editTextName = view?.findViewById(R.id.et_titulo_de_registro)
        fecha = view?.findViewById(R.id.textview_record_date_selection)
        monto = view?.findViewById(R.id.et_Monto)



        editTextDescripcion = view?.findViewById(R.id.editText_record_description)


        // Validar campos en formulario

        if (editTextName?.text!!.isEmpty() or fecha?.text!!.isEmpty() or monto?.text!!.isEmpty() or editTextDescripcion?.text!!.isEmpty()) {
            Toast.makeText(this.context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            val sharedPreferences = this.context?.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            val currentFirebaseUser = sharedPreferences?.getString(APP_NAME, FIREBASE_CURRENT_USER_KEY)
            Log.i(ADD_RECORD_FRAGMENT, "CurrentUser is ${currentFirebaseUser.toString()}")


        } else {

            // INICIALIZANDO INSTANCIA DE FIREBASE

            val firebaseDB = FirebaseFirestore.getInstance()

            // SHAREDPREFERENCES
            val sharedPreferences = this.context?.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
            var viajeID = sharedPreferences!!.getString(FIREBASE_TRAVEL_ID, null)
            val currentFirebaseUser = sharedPreferences.getString(APP_NAME, FIREBASE_CURRENT_USER_KEY)
            val currentFirebaseEmailUser = sharedPreferences.getString(APP_NAME, FIREBASE_USER_EMAIL_LOGGED_IN_KEY)

            // Elementos de UI

            val recordName: String? = editTextName?.text.toString()
            val recordDate: String? = fecha?.text.toString()
            val recordAmmount: String? = monto?.text.toString()
            val recordCategory: String? = radioGroup?.checkedRadioButtonId.toString()
            val recordPhoto: String? = "Esto deberia ser el URI de la foto"
            val recordDescription: String = editTextDescripcion?.text.toString()
            val recordDateRegistered: String? = "" // Falta obtener fecha actual al momento de crear el record
            val recordDateLastUpdate: String? = "" // Falta obtener fecha de modificacion

            // Envio de Datos usando el Modelo de Datos

            val addNewRecord = Record(
            recordName!!,
            recordDate!!,
            recordAmmount!!,
            recordCategory!!,
            recordPhoto!!,
            recordDescription,
            recordDateRegistered!!,
            recordDateLastUpdate!!
            )

            firebaseDB.collection("e-Tracker").document(viajeID!!).collection("record")
                .add(addNewRecord)
                .addOnFailureListener {
                    Toast.makeText(this.context, "Fallo", Toast.LENGTH_SHORT).show()
                    Log.i(ADD_RECORD_FRAGMENT, "Error $it")
                }
                .addOnSuccessListener {
                    Toast.makeText(this.context, "Exito", Toast.LENGTH_SHORT).show()
                    Log.i(ADD_RECORD_FRAGMENT, "Registro agregado existosamente con ID de Viaje $FIREBASE_TRAVEL_ID")
                }
        }

    }

    private fun radioButtonSelection(radioButtonId: Int){

        if (radioButtonId == radioButtonFood?.id){
            radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food)
            radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel_gray)
            radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation_gray)
            radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

            radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_selected_background_gradient)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_background_gradient)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

        } else if (radioButtonId == radioButtonHotel?.id) {
            radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food_gray)
            radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel)
            radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation_gray)
            radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

            radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_selected_background_gradient)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_background_gradient)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

        } else if (radioButtonId == radioButtonTransportation?.id) {
            radioButtonFood?.setButtonDrawable(R.drawable.ic_category_food_gray)
            radioButtonHotel?.setButtonDrawable(R.drawable.ic_category_hotel_gray)
            radioButtonTransportation?.setButtonDrawable(R.drawable.ic_category_transportation)
            radioButtonOther?.setButtonDrawable(R.drawable.ic_category_other_gray)

            radioButtonFood?.setBackgroundResource(R.drawable.ic_category_food_background_gradient)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_category_hotel_background_gradient)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_category_transportation_selected_background_gradient)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_category_other_background_gradient)

        } else if (radioButtonId == radioButtonOther?.id) {
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
                // val finishdate = mDateEnd

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
