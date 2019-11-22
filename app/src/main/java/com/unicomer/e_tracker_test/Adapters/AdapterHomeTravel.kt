package com.unicomer.e_tracker_test.Adapters

import java.lang.Integer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.unicomer.e_tracker_test.models.Record
import com.unicomer.e_tracker_test.R
import com.unicomer.e_tracker_test.viewmodels.TravelViewModel

class AdapterHomeTravel(
    private val viewModel: TravelViewModel,
    options:FirestoreRecyclerOptions<Record>):
    FirestoreRecyclerAdapter<Record, AdapterHomeTravel.HomeTravelHolder>(options){

    private var viewDataBinding: ViewDataBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTravelHolder {
        val layoutInflaterx = LayoutInflater.from(parent.context)
        viewDataBinding = DataBindingUtil.inflate(layoutInflaterx, viewType, parent, false)
        return HomeTravelHolder(viewDataBinding!!)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.list_row_new_record
    }

    override fun onBindViewHolder(holder: HomeTravelHolder, position: Int, model: Record) {
        holder.bind(viewModel, position, model)
    }

    inner class HomeTravelHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TravelViewModel, index: Int, model: Record) {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, index)
            binding.setVariable(BR.model, model)
            binding.executePendingBindings()
        }
    }

}