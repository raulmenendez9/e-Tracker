package com.unicomer.e_tracker_test

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.unicomer.e_tracker_test.models.Record


class DetailRecordFragment : Fragment() {
    //objeto que contiene los datos del detalle
    lateinit var objDetailData:Record
    private var listener: OnFragmentInteractionListener? = null
    //variables del layout
    var titleCatDetail: TextView?=null
    var nameDetail: TextView?=null
    var dateDetail: TextView?=null
    var categoryDetail: TextView?=null
    var imageCatDetail:ImageView?=null
    var descriptionDetail: TextView?=null
    var priceDetail: TextView?=null
    var photoDetail: ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        photoDetail = view.findViewById(R.id.photoRecordDetail)
        fillDetail()
    }
    private fun fillDetail(){
        //Seteo de datos
        nameDetail!!.text = objDetailData.recordName
        dateDetail!!.text = objDetailData.recordDate
        descriptionDetail!!.text= objDetailData.recordDescription
        priceDetail!!.text="$"+objDetailData.recordMount
        //Se asigna la imagen
        Glide.with(this).load(objDetailData.recordPhoto).into(photoDetail!!)
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
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

/*
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
*/
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(obj:Record) : DetailRecordFragment{
            //se instancia el fragment con el objeto de tipo Record que viene del adapter
            val fragment = DetailRecordFragment()
            fragment.objDetailData = obj
            return  fragment
        }

    }
}
