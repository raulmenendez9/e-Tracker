package com.unicomer.e_tracker_test.fragments

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.constants.ADD_RECORD_FRAGMENT
import com.unicomer.e_tracker_test.constants.FIREBASE_TRAVEL_ID
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.travel_registration.DatePickerFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddRecordFragment : Fragment() {



    // Objeto que contiene los datos de un detalle anteriomente guardado
    lateinit var objectRecordDetail: Record
    lateinit var recordId: String
    lateinit var travelId: String
    private var recordExists: Boolean? = null

    // Fragment Listener
    private var listener: OnFragmentInteractionListener? = null

    //FireStorage
    var storageRef: StorageReference = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("record-image/${System.currentTimeMillis()}_image_ticket.png")//nombre del archivo a publicar

    // Elementos de UI
    private var editTextName: EditText? = null
    private var fecha: TextView? = null
    private var monto: EditText? = null
    private var editTextDescripcion: EditText? = null
    private var pathImage: TextView? = null

    // Contenedor RadioGroup
    private var radioGroup: RadioGroup? = null

    // UI de RadioButton
    private var radioButtonFood: RadioButton? = null
    private var radioButtonHotel: RadioButton? = null
    private var radioButtonTransportation: RadioButton? = null
    private var radioButtonOther: RadioButton? = null

    // Variables para ID de cada RadioButton
    private val buttonFoodId: Int? = 0 //0
    private val buttonHotelId: Int? = 2 //1
    private val buttonTransportationId: Int? = 1 //2
    private val buttonOtherId: Int? = 3 //3

    // Boton Tomar Foto

    private var buttonTakePhoto: Button? = null
    private val SELECT_IMAGE : Int =23748
    private val TAKE_PICTURE : Int =5000
    private var mCurrentPhotoPath: String? = null
    private var photoUri: Uri? = null
    private var imageDir: String? = null

    // Boton Agregar Registro

    private var buttonAddRecord: Button? = null

    // DatePicker
    private var datePicker: TextView? = null
    // private var finishDate: EditText?=null
    private var unbinder: Unbinder? = null

    // Variables para DatePicker
    var mDateStart: String? = null
    var mDateEnd: String? = null

    // Variable del contexto
    private var mycontext : FragmentActivity? = null



    override fun onAttach(context: Context) {
        super.onAttach(context)

        Log.i(ADD_RECORD_FRAGMENT, "In method onAttach")

        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(ADD_RECORD_FRAGMENT, "In method OnCreate")

        Log.i(ADD_RECORD_FRAGMENT, "El record es ${recordExists.toString()}")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_registro, container, false)

        Log.i(ADD_RECORD_FRAGMENT, "In method onCreateView")

        //pathImage = view?.findViewById(R.id.pathImage)

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
        buttonTakePhoto?.setOnClickListener{

        //Permisos
            if (permissionValidation()){
                buttonTakePhoto!!.isEnabled
                dialogPhoto()
            }else {
                buttonTakePhoto!!.isEnabled
            }
        }

        buttonAddRecord = view?.findViewById(R.id.btn_agregar_registro)
        buttonAddRecord?.setOnClickListener {

        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(ADD_RECORD_FRAGMENT, "In method onViewCreated")


        // Aqui se inicializa este Fragment
        // Aqui se comprueba si el record ya existe y debe ser inicializado
        // Si no existe entonces se puede crear desde cero
        getInitialData(recordExists!!)

        datePicker = view.findViewById(R.id.textview_record_date_selection)

        // DatePicker
        ButterKnife.bind(this, view)
        unbinder = ButterKnife.bind(this, view)

        datePicker!!.setOnClickListener{
            openDateRangePicker()
        }


    }


    override fun onDestroy() {

        Log.i(ADD_RECORD_FRAGMENT, "In method onDestroy")


        super.onDestroy()
        unbinder!!.unbind()
    }


    override fun onDetach() {

        Log.i(ADD_RECORD_FRAGMENT, "In method onDetach")

        super.onDetach()
        listener = null
    }


    private fun getInitialData(recordExists: Boolean){

        // Variable de Estado que viene desde el fragment anterior
        var recordExists = recordExists


        // Init UI
        editTextName = view?.findViewById(R.id.et_titulo_de_registro)
        fecha = view?.findViewById(R.id.textview_record_date_selection)
        monto = view?.findViewById(R.id.et_Monto)
        editTextDescripcion = view?.findViewById(R.id.editText_record_description)

        if (recordExists) {
            // Si el record SI EXISTE y es TRUE entonces se ejecuta este codigo


            // Inicializar el UI con la informacion que trae el objeto

            editTextName?.setText(objectRecordDetail.recordName)
            fecha?.setText(objectRecordDetail.recordDate)
            monto?.setText(objectRecordDetail.recordMount)
            radioGroup!!.check(objectRecordDetail.recordCategory.toInt())
            editTextDescripcion?.setText(objectRecordDetail.recordDescription)


            // Aqui se inicializa la variable global imageDir para que pueda ser comparada

            var initialImageDirFromFirebase = objectRecordDetail.recordPhoto
            imageDir = objectRecordDetail.recordPhoto

            Log.i(ADD_RECORD_FRAGMENT, "este es el imageDir $imageDir despues de inicializar")

            // Cambiar texto a los botones
            buttonTakePhoto!!.setText("MODIFICAR FOTO")
            buttonAddRecord!!.setText("ACTUALIZAR")


            // Hora de actualizar los datos
            // Si el usuario cambia la informacion entonces se comprueba y se reemplaza
            // la nueva informacion en una peticion a Firebase

            buttonTakePhoto!!.setOnClickListener {

                // Pedir los permisos para abrir la camara

                if (permissionValidation()) {

                    buttonTakePhoto!!.isEnabled
                    dialogPhoto()

                } else {
                    buttonTakePhoto!!.isEnabled
                }
            }

            // A partir de aqui el URI de la foto podria haber cambiado y es necesario comprobar

            buttonAddRecord!!.setOnClickListener {

                Log.i(ADD_RECORD_FRAGMENT, "imageDir es $imageDir y initialImageDir es $initialImageDirFromFirebase")

                var radioId = radioGroup?.checkedRadioButtonId.toString()

                // Comprobar nuevamente que los campos no esten vacios o solamente sean espacios

                if (editTextName?.text!!.isBlank()
                    or fecha?.text!!.isBlank()
                    or monto?.text!!.isBlank()
                    or editTextDescripcion?.text!!.isBlank()
                    or radioId.equals("-1")) {
                    Toast.makeText(this.context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()

                } else if (imageDir.equals(initialImageDirFromFirebase)) {


                    // Si el URI de la foto no se cambia, es decir, el usuario no cambia la foto
                    // se ejecuta este codigo

                    val firebaseDB = FirebaseFirestore.getInstance()

                    // Elementos de UI

                    val recordName: String? = editTextName?.text.toString()
                    val recordDate: String? = fecha?.text.toString()
                    val recordAmount: String? = monto?.text.toString()
                    val recordCategory: String? = radioGroup?.checkedRadioButtonId.toString()
                    val recordDescription: String = editTextDescripcion?.text.toString()
                    val recordDateLastUpdate: String? = Timestamp.now().toDate().toString()

                    firebaseDB.collection("e-Tracker").document(travelId).collection("record").document(recordId)
                        .update(
                            "recordName", recordName,
                            "recordDate", recordDate,
                            "recordMount", recordAmount,
                            "recordCategory", recordCategory,
                            "recordDescription", recordDescription,
                            "recordDateLastUpdate", recordDateLastUpdate
                        )
                        .addOnFailureListener {
                            Toast.makeText(this.context, "El record no pudo ser actualizado.", Toast.LENGTH_SHORT).show()

                        }
                        .addOnSuccessListener {
                            Toast.makeText(this.context, "El record ha sido actualizado.", Toast.LENGTH_SHORT).show()

                            Handler().postDelayed({
                                activity!!.supportFragmentManager.popBackStack()
                            }, 1000)
                        }

                } else {

                    // Si todos los campos estan correctos, actualizar la informacion en Firebase
                    // Si el URI de la foto ha sido cambiada entonces se ejecuta este codigo

                    // INICIALIZANDO INSTANCIA DE FIREBASE

                    val firebaseDB = FirebaseFirestore.getInstance()
                    val image = Uri.parse(imageDir)
                    imageRef.putFile(image).addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener{taskSnapshot ->

                            Log.i(ADD_RECORD_FRAGMENT, "SI LLEGO HASTA AQUI")

                            // Elementos de UI

                            val recordName: String? = editTextName?.text.toString()
                            val recordDate: String? = fecha?.text.toString()
                            val recordAmount: String? = monto?.text.toString()
                            val recordCategory: String? = radioGroup?.checkedRadioButtonId.toString()
                            val recordPhoto =   taskSnapshot.result
                            val recordDescription: String = editTextDescripcion?.text.toString()
                            val recordDateLastUpdate: String? = Timestamp.now().toDate().toString()

                            // Envio de Datos


                            firebaseDB.collection("e-Tracker").document(travelId).collection("record").document(recordId)
                                .update(
                                    "recordName", recordName,
                                    "recordDate", recordDate,
                                    "recordMount", recordAmount,
                                    "recordCategory", recordCategory,
                                    "recordPhoto", "$recordPhoto",
                                    "recordDescription", recordDescription,
                                    "recordDateLastUpdate", recordDateLastUpdate
                                )
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Fallo", Toast.LENGTH_SHORT).show()
                                    Log.i(ADD_RECORD_FRAGMENT, "Error $it")
                                }
                                .addOnSuccessListener {
                                    Toast.makeText(this.context, "El registro ha sido actualizado", Toast.LENGTH_SHORT).show()
                                    Log.i(ADD_RECORD_FRAGMENT, "Registro agregado existosamente con ID de Viaje $FIREBASE_TRAVEL_ID")
                                    Handler().postDelayed({
                                        activity!!.supportFragmentManager.popBackStack()
                                    }, 1000)
                                }
                        }
                    }

                }
            }



        } else {
            // Si el record NO EXISTE y es FALSE entonces se ejecuta este codigo

            // Inicializar UI
            editTextName = view?.findViewById(R.id.et_titulo_de_registro)
            fecha = view?.findViewById(R.id.textview_record_date_selection)
            monto = view?.findViewById(R.id.et_Monto)
            editTextDescripcion = view?.findViewById(R.id.editText_record_description)
            var radioId = radioGroup?.checkedRadioButtonId.toString()


            buttonAddRecord!!.setOnClickListener {

            // Validar campos en formulario

            if (editTextName?.text!!.isBlank()
                or fecha?.text!!.isBlank()
                or monto?.text!!.isBlank()
                or editTextDescripcion?.text!!.isBlank()
                or radioId.equals("-1")
                or imageDir.equals(null)) {
                Toast.makeText(this.context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()

                Log.i(ADD_RECORD_FRAGMENT, "Radio category is ${radioGroup?.checkedRadioButtonId.toString()}")


            } else {

                    val firestoreDB = FirebaseFirestore.getInstance()
                    var image = Uri.parse(imageDir)

                    imageRef.putFile(image).addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener {taskSnapshot ->

                            // Elementos de UI

                            val recordName: String? = editTextName?.text.toString()
                            val recordDate: String? = fecha?.text.toString()
                            val recordAmount: String? = monto?.text.toString()
                            val recordCategory: String? = radioGroup?.checkedRadioButtonId.toString()
                            val recordPhoto =  taskSnapshot.result
                            val recordDescription: String = editTextDescripcion?.text.toString()
                            val recordDateRegistered: String? = Timestamp.now().toDate().toString()
                            val recordDateLastUpdate: String? = "" // Falta obtener fecha de modificacion

                            // Envio de datos usando el data class

                            val addNewRecord = Record(
                                recordName!!,
                                recordDate!!,
                                recordAmount!!,
                                recordCategory!!,
                                "$recordPhoto",
                                recordDescription,
                                recordDateRegistered!!,
                                recordDateLastUpdate!!
                            )

                            firestoreDB.collection("e-Tracker").document(travelId).collection("record")
                                .add(addNewRecord)
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "La creación del registro ha fallado", Toast.LENGTH_SHORT).show()
                                    Log.e(ADD_RECORD_FRAGMENT, "Error en $it")
                                }
                                .addOnSuccessListener {
                                    Toast.makeText(this.context, "Registro creado exitosamente.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }
    }



    //Permisos de camara, lectura y escritura
    private fun permissionValidation(): Boolean {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true
        }
        if ((checkSelfPermission(context!!, CAMERA)==PackageManager.PERMISSION_GRANTED)&&
            (checkSelfPermission(context!!, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)&&
            (checkSelfPermission(context!!, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,CAMERA)||ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,READ_EXTERNAL_STORAGE)){
            permissionDialog()

        }else{requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA),100)}

        return false
    }

    fun permissionDialog(){
        val dialogPermission: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogPermission.setTitle("Permisos Desactivados")
        dialogPermission.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la app")
        dialogPermission.setPositiveButton("Aceptar", DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA),100)
        })
        dialogPermission.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==100){
            if (grantResults.size == 3 && grantResults[0] ==PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2]== PackageManager.PERMISSION_GRANTED){
                buttonTakePhoto!!.isEnabled
            }
        }
    }
    //Fin de permisos

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }


    private fun radioButtonSelection(radioButtonId: Int){

        if (radioButtonId == radioButtonFood?.id){

            radioButtonFood?.setBackgroundResource(R.drawable.ic_cat_food_gradient_on)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_cat_hotel_gradient_off)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_cat_transportation_gradient_off)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_cat_other_gradient_off)

        } else if (radioButtonId == radioButtonHotel?.id) {

            radioButtonFood?.setBackgroundResource(R.drawable.ic_cat_food_gradient_off)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_cat_hotel_gradient_on)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_cat_transportation_gradient_off)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_cat_other_gradient_off)

        } else if (radioButtonId == radioButtonTransportation?.id) {

            radioButtonFood?.setBackgroundResource(R.drawable.ic_cat_food_gradient_off)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_cat_hotel_gradient_off)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_cat_transportation_gradient_on)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_cat_other_gradient_off)

        } else if (radioButtonId == radioButtonOther?.id) {

            radioButtonFood?.setBackgroundResource(R.drawable.ic_cat_food_gradient_off)
            radioButtonHotel?.setBackgroundResource(R.drawable.ic_cat_hotel_gradient_off)
            radioButtonTransportation?.setBackgroundResource(R.drawable.ic_cat_transportation_gradient_off)
            radioButtonOther?.setBackgroundResource(R.drawable.ic_cat_other_gradient_on)
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

    private fun dialogPhoto(){
        try {
            var photoFile: File? = null
            val items = arrayOf<CharSequence>(getString(R.string.gallery), getString(
                R.string.camera
            ))
            val builder :AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.select_image))
            builder.setItems(items) { _: DialogInterface, i: Int ->
                when(i){
                    0-> {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        startActivityForResult(intent,SELECT_IMAGE)
                    }
                    1->{
                        val camaraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (camaraIntent.resolveActivity(activity!!.packageManager) != null){
                            try {
                                photoFile = createImageFile()

                            }catch (ex:IOException){ }
                        }
                        if (photoFile != null){
                            val values = ContentValues()
                            values.put(MediaStore.Images.Media.TITLE,"Ticket")
                            values.put(MediaStore.Images.Media.DESCRIPTION,"Photo taken on ${System.currentTimeMillis()}")

                            photoUri = activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                            camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
                            startActivityForResult(camaraIntent, TAKE_PICTURE)
                            imageDir = photoUri.toString()
                            Log.i(ADD_RECORD_FRAGMENT, "uri image = $photoUri.")
                        }
                    }
                }
            }
            val alertDialog:AlertDialog = builder.create()
            alertDialog.show()
        }
        catch (e:Exception){
            Log.e("ERROR", "$e")
            Toast.makeText(this.context, "ERROR", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile():File{
        val timestamp =SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName ="JPEG_$timestamp _"
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName,".jpg",storageDir)
        mCurrentPhotoPath=image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {

            if (requestCode==SELECT_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                val selectedImage: Uri = data.data!!
                imageDir = selectedImage.toString()

            }
            if (requestCode ==TAKE_PICTURE && resultCode == Activity.RESULT_OK ) {
                val selectedImage: Uri = data?.data!!
                pathImage!!.text = selectedImage.toString()
            }
        } catch (e:java.lang.Exception){
            Log.i("ERROR", "message: ${e.message}")
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)

        fun createNewRecord()
        fun updateExistingRecord()

    }

    companion object {
        @JvmStatic
        fun updateRecord(objectRecordDetail: Record, recordId: String, travelId: String, recordExists: Boolean): AddRecordFragment {
            // Instanciar este fragment y recibir un objeto que contenga los datos de un registro anterior
            val fragment =
                AddRecordFragment()
            fragment.objectRecordDetail = objectRecordDetail
            fragment.recordId = recordId
            fragment.travelId = travelId
            fragment.recordExists = recordExists
            return fragment
        }

        @JvmStatic
        fun createRecord(recordExists: Boolean): AddRecordFragment {
            // Crear nuevo record
            val fragment = AddRecordFragment()
            fragment.recordExists = recordExists
            return fragment
        }


    }
}

