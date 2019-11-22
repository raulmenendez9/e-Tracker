package com.unicomer.e_tracker_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unicomer.e_tracker_test.data.AppDatabase
import com.unicomer.e_tracker_test.data.RecordRepository
import com.unicomer.e_tracker_test.databinding.FragmentHomeTravelBinding
import com.unicomer.e_tracker_test.viewmodels.TravelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class HomeTravelFragment : Fragment(){

    private lateinit var binding: FragmentHomeTravelBinding
    private lateinit var viewModel: TravelViewModel

    private var repository: RecordRepository? = null
    private var databaseReference: AppDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeTravelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(TravelViewModel::class.java)
        }?: throw Exception("invalid-class")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = AppDatabase.getInstance(requireContext())
        repository = RecordRepository.recordInstance(databaseReference?.travelDataDao()!!)

        viewModel.let { binding.viewModel = it }

        viewModel.initializeData()

        viewModel.dataId?.observe(viewLifecycleOwner, Observer {

            Log.i("data-id", "data -> $it")

        })

        viewModel.data.observe(viewLifecycleOwner, Observer { collection ->
            if (collection.size != 0) {
                binding.travel = collection[0]
                viewModel.setAdapter()
            }
        })

        viewModel.record.observe(viewLifecycleOwner, Observer {

            CoroutineScope(Dispatchers.IO).launch {
                repository?.createRecord(it)
            }

            binding.recyclerRecord.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = viewModel.homeAdapter
            }
        })



        CoroutineScope(Dispatchers.IO).launch {
            Log.i("data-persistance", "sizeOf: ${repository?.records()?.size}")
        }




        viewModel.recordSelected.value = null
        viewModel.recordSelected.observe(viewLifecycleOwner, Observer { action ->
            if (action != null)  {
                // Log.i("data-selected", "data -> ${action.recordName}")
                findNavController().navigate(R.id.action_homeTravelFragment_to_detailFragment)
            }

        })

    }



    override fun onStart() {
        super.onStart()
        viewModel.homeAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        viewModel.homeAdapter!!.stopListening()
    }

}
