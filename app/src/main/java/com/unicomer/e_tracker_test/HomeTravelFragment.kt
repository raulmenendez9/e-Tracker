package com.unicomer.e_tracker_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.unicomer.e_tracker_test.databinding.FragmentHomeTravelBinding
import com.unicomer.e_tracker_test.viewmodels.TravelViewModel


class HomeTravelFragment : Fragment(){

    private lateinit var binding: FragmentHomeTravelBinding
    private lateinit var viewModel: TravelViewModel

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

        viewModel.let { binding.viewModel = it }

        viewModel.initializeData()

        viewModel.dataId?.observe(viewLifecycleOwner, Observer {

            Log.i("data-id", "data -> $it")

        })

        viewModel.data.observe(viewLifecycleOwner, Observer { collection ->

            if (collection.size != 0) {

                viewModel.setAdapter()

                binding.recyclerRecord.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = viewModel.homeAdapter
                }
            }
        })

        viewModel.defaultData()

    }

    override fun onStart() {
        super.onStart()
        viewModel.homeAdapter!!.startListening()
    }

    override fun onPause() {
        super.onPause()
        viewModel.homeAdapter!!.stopListening()
    }

}
