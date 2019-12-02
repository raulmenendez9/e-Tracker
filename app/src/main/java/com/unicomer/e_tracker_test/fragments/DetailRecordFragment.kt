package com.unicomer.e_tracker_test.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
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
    var btnDelete: Button?=null
    var btnEditRecord: Button? = null

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
        imageCatDetail = view.findViewById(R.id.imageCategoryDetail)
        photoDetail = view.findViewById(R.id.photoRecordDetail)
        btnDelete = view.findViewById(R.id.deleteButtonDetail)
        btnEditRecord = view.findViewById(R.id.editButtonDetail)
        fillDetail()
    }

    override fun onResume() {
        super.onResume()
        btnDelete!!.setOnClickListener {
            Log.i("ELMEROID","el id record es: $idRecord y el id del viaje es: $idTravel y el ${objDetailData}")
            showDialog(idRecord, idTravel, objDetailData)

        }

        btnEditRecord!!.setOnClickListener {
            listener?.updateExistingRecord(objDetailData, idRecord, idTravel, true)
            // CallFragment().addFragment(this.fragmentManager!!, AddRecordFragment.updateRecord(objDetailData, idRecord, idTravel, true), true, true, true)
        }

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

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }
    fun showDialog(id: String, idTravel: String, obj:Record){
        val fm = this.fragmentManager
        val dialog = DeleteRecordDialog.newInstance(id, idTravel, obj)
        dialog.show(fm!!, DELETE_DIALOG)

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
        fun updateExistingRecord(objDetailData: Record, idRecord: String, idTravel: String, recordExists: Boolean)
    }

    companion object {
        @JvmStatic
        fun newInstance(obj:Record, id:String, idTravel:String) : DetailRecordFragment {
            //se instancia el fragment con el objeto de tipo Record que viene del adapter
            val fragment =
                DetailRecordFragment()
            fragment.objDetailData = obj
            fragment.idRecord = id
            fragment.idTravel = idTravel
            return  fragment
        }

    }
}
