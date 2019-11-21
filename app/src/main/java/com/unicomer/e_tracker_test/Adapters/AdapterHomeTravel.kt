package com.unicomer.e_tracker_test.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.Models.Record
import com.unicomer.e_tracker_test.viewmodels.TravelViewModel
import kotlinx.android.extensions.LayoutContainer

class AdapterHomeTravel(
    private val viewModel: TravelViewModel,
    options:FirestoreRecyclerOptions<Record>):
    FirestoreRecyclerAdapter<Record, AdapterHomeTravel.HomeTravelHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTravelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_row_new_record, parent, false)
        return HomeTravelHolder(view)
    }

    override fun onBindViewHolder(holder: HomeTravelHolder, position: Int, model: Record) {
        holder.apply {
            recordName.text = model.recordName.take(17) //corta el string mostrando los primeros 17 caracteres
            recordPrice.text = model.recordMount
            recordDate.text = model.recordDate

            when(model.recordCategory){
                "0" ->{
                    imageCat.setImageResource(R.drawable.ic_cat_food)
                }
                "1" ->{
                    imageCat.setImageResource(R.drawable.ic_cat_car)
                }
                "2" ->{
                    imageCat.setImageResource(R.drawable.ic_cat_hotel)
                }
                "3" ->{
                    imageCat.setImageResource(R.drawable.ic_cat_other)
                }
            }
        }
    }

    inner class HomeTravelHolder(override val containerView: View):RecyclerView.ViewHolder(containerView), LayoutContainer{
        var recordName: TextView = containerView.findViewById(R.id.txt_record_name)
        var recordPrice: TextView = containerView.findViewById(R.id.txt_record_price)
        var recordDate: TextView = containerView.findViewById(R.id.txt_record_date)
        var imageCat: ImageView = containerView.findViewById(R.id.image_record_cat)
    }
    //interface ShowDataInterface{
    //}
}