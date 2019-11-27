package com.unicomer.e_tracker_test.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.unicomer.e_tracker_test.Models.Travel
import com.unicomer.e_tracker_test.R
import kotlinx.android.extensions.LayoutContainer

class AdapterHistory(options: FirestoreRecyclerOptions<Travel>) :
    FirestoreRecyclerAdapter<Travel, AdapterHistory.CollectionHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_row_old_travel, parent,false)

        return CollectionHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionHolder, position: Int, model: Travel) {
        holder.apply {
            paisActual.text = model.originCountry
            paisDestino.text = model.destinyCountry
            monto.text = model.cash
            fecha.text = model.initialDate


        }
    }

    inner class CollectionHolder( override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        var paisActual: TextView = containerView.findViewById(R.id.textPaisActual)
        var paisDestino: TextView = containerView.findViewById(R.id.textPaisDestino)
        var monto: TextView = containerView.findViewById(R.id.textMontoViaje)
        var fecha: TextView = containerView.findViewById(R.id.textFecha)
    }


}


