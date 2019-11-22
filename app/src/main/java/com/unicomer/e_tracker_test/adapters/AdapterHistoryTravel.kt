package com.unicomer.e_tracker_test.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.unicomer.e_tracker_test.Models.HistoryTravel
import com.unicomer.e_tracker_test.R
import kotlinx.android.extensions.LayoutContainer

class AdapterHistoryTravel(options: FirestoreRecyclerOptions<HistoryTravel>):
    FirestoreRecyclerAdapter<HistoryTravel, AdapterHistoryTravel.HistoryTravelHolder>(options){
    //var listener: ShowDataInterface? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryTravelHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_row_old_travel, parent, false)
        return HistoryTravelHolder(view)
    }
    override fun onBindViewHolder(holder: HistoryTravelHolder, position: Int, model: HistoryTravel) {
        holder.apply {
            paisActual.text=model.originCountry
            paisDestino.text=model.destinyCountry
            monto.text=model.cash
            fecha.text=model.initialDate
            Log.i("posiciones en historial","$position")

        }
    }

    inner class HistoryTravelHolder(override val containerView: View): RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        var paisActual: TextView = containerView.findViewById(R.id.textPaisActual)
        var paisDestino: TextView = containerView.findViewById(R.id.textPaisDestino)
        var monto: TextView = containerView.findViewById(R.id.textMontoViaje)
        var fecha: TextView = containerView.findViewById(R.id.textFecha)
        var estado:View =containerView.findViewById(R.id.estadoViaje)

    }

}