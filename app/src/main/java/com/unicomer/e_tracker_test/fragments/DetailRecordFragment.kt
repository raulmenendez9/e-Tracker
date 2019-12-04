package com.unicomer.e_tracker_test.fragments

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.classes.CallFragment
import com.unicomer.e_tracker_test.constants.DELETE_DIALOG
import com.unicomer.e_tracker_test.dialogs.DeleteRecordDialog
import com.unicomer.e_tracker_test.models.Record




class DetailRecordFragment : Fragment() {

    //objeto que contiene los datos del detalle
    lateinit var objDetailData:Record
    //obtnego el id del viaje
    lateinit var idRecord: String
    lateinit var idTravel: String
    lateinit var isActive: String //viaje actual: 0=si, 0!=no

    private var listener: OnFragmentInteractionListener? = null
    //variables del layout
    var titleCatDetail: TextView?=null
    var nameDetail: TextView?=null
    var dateDetail: TextView?=null
    var imageCatDetail:ImageView?=null
    var descriptionDetail: TextView?=null
    var priceDetail: TextView?=null
    //var photoDetail: ImageView?=null
    var btnDelete: Button?=null
    var btnEditRecord: Button? = null
    var btnEdit:Button?=null
    var photo: PhotoView?=null
    var container: FrameLayout?=null
    var btnArrow: ImageView?=null
    var descriptionTitle: TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instancia de animacion

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_detail_record, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameDetail = view.findViewById(R.id.recordNameDetail)
        dateDetail = view.findViewById(R.id.recordDateDetail)
        descriptionDetail = view.findViewById(R.id.recordDescriptionDetail)
        priceDetail = view.findViewById(R.id.recordPriceDetail)
        titleCatDetail = view.findViewById(R.id.categoyTittleDetail)
        imageCatDetail = view.findViewById( R.id.imageCategoryDetail)
        btnDelete = view.findViewById(R.id.deleteButtonDetail)
        btnEditRecord = view.findViewById(R.id.editButtonDetail)
        btnEdit = view.findViewById(R.id.editButtonDetail)
        photo = view.findViewById(R.id.photoRecordDetail)
        btnArrow = view.findViewById(R.id.arrow_detail)
        container = view.findViewById(R.id.frameDetailContainer)
        descriptionTitle = view.findViewById(R.id.textViewDescripTittle)
        fillDetail()
        //animacion para la card que contiene la info(solo de entrada)
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        container!!.startAnimation(animation)
        //verifica de donde proviene para saber si mostrar o no los botones de edit/delete
        if(isActive!="0"){
            btnDelete!!.visibility = View.GONE
            btnEdit!!.visibility = View.GONE
        }
        btnArrow!!.setOnClickListener {
            //animacion para ocultar parte del frame al dar clic en la flecha
            animtationFrame()
        }
        photo!!.setOnClickListener {
            //animacion al dar clic en la foto
            animtationFrame()
        }

    }

    override fun onResume() {
        super.onResume()
        btnDelete!!.setOnClickListener {
            showDialog(idRecord, idTravel, objDetailData)

        }

        btnEditRecord!!.setOnClickListener {
            listener?.updateExistingRecord(objDetailData, idRecord, idTravel, true)
        }

    }
    private fun fillDetail(){
        //Seteo de datos
        nameDetail!!.text = objDetailData.recordName
        dateDetail!!.text = objDetailData.recordDate
        descriptionDetail!!.text= objDetailData.recordDescription
        priceDetail!!.text="$"+objDetailData.recordMount
        //Se asigna la imagen
        Glide.with(this).load(objDetailData.recordPhoto).into(photo!!)
        //photo!!.setImageResource(photoDetail!!.id)
        //se toma la categoria y dependiendo de la que se obtiene se setea
        when(objDetailData.recordCategory){
            "0"->{
                imageCatDetail!!.setImageResource(R.drawable.ic_cat_food)
                titleCatDetail!!.text = "Comida"
                titleCatDetail!!.setTextColor(Color.parseColor("#FEC180"))
            }

            "1"->{
                imageCatDetail!!.setImageResource(R.drawable.ic_cat_car)
                titleCatDetail!!.text = "Transporte"
                titleCatDetail!!.setTextColor(Color.parseColor("#FBE339"))
            }

            "2"->{
                imageCatDetail!!.setImageResource(R.drawable.ic_cat_hotel)
                titleCatDetail!!.text = "Hospedaje"
                titleCatDetail!!.setTextColor(Color.parseColor("#BAFC8B"))
            }

            "3"->{
                imageCatDetail!!.setImageResource(R.drawable.ic_cat_other)
                titleCatDetail!!.text = "Otro"
                titleCatDetail!!.setTextColor(Color.parseColor("#E39BFC"))
            }
        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }
    fun showDialog(id: String, idTravel: String, obj:Record){
        val fm = this.fragmentManager
        val dialog = DeleteRecordDialog.newInstance(id, idTravel, obj)
        dialog.show(fm!!, DELETE_DIALOG)

    }
    fun animtationFrame(){
        if (btnArrow!!.rotation==0f){
            val animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            container!!.startAnimation(animation2)
            Handler().postDelayed({
                nameDetail!!.visibility = View.VISIBLE
                dateDetail!!.visibility = View.VISIBLE
                descriptionDetail!!.visibility = View.VISIBLE
                priceDetail!!.visibility = View.VISIBLE
                titleCatDetail!!.visibility = View.VISIBLE
                imageCatDetail!!.visibility = View.VISIBLE
                descriptionTitle!!.visibility = View.VISIBLE
                btnArrow!!.rotation = 180f
                if(isActive=="0"){
                    btnDelete!!.visibility = View.VISIBLE
                    btnEditRecord!!.visibility = View.VISIBLE
                    btnEdit!!.visibility = View.VISIBLE
                }
            }, 250)
        }else{
            val animation2 = AnimationUtils.loadAnimation(context, R.anim.slide_down)
            container!!.startAnimation(animation2)
            Handler().postDelayed({
                nameDetail!!.visibility = View.GONE
                dateDetail!!.visibility = View.GONE
                descriptionDetail!!.visibility = View.GONE
                priceDetail!!.visibility = View.GONE
                titleCatDetail!!.visibility = View.GONE
                imageCatDetail!!.visibility = View.GONE
                btnDelete!!.visibility = View.GONE
                btnEditRecord!!.visibility = View.GONE
                btnEdit!!.visibility = View.GONE
                descriptionTitle!!.visibility = View.GONE
                btnArrow!!.rotation = 0f
            }, 250)
        }
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
        fun updateExistingRecord(objDetailData: Record, idRecord: String, idTravel: String, recordExists: Boolean)
    }

    companion object {
        @JvmStatic
        fun newInstance(obj:Record, id:String, idTravel:String, isActive:String) : DetailRecordFragment{
            //se instancia el fragment con el objeto de tipo Record que viene del adapter
            val fragment = DetailRecordFragment()
            fragment.objDetailData = obj
            fragment.idRecord = id
            fragment.idTravel = idTravel
            fragment.isActive = isActive
            return  fragment
        }

    }
}
