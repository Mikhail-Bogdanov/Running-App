package com.example.runningapp.fragments

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningapp.R
import com.example.runningapp.adapter.MainRecyclerViewAdapter
import com.example.runningapp.databinding.FragmentMainBinding
import com.example.runningapp.other.Constants.TOAST_TEXT_BACKGROUND_PERM
import com.example.runningapp.other.MyPermissions
import com.example.runningapp.other.SortType
import com.example.runningapp.other.SortType.*
import com.example.runningapp.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val typeArray: Array<SortType> = arrayOf(DATE, SPEED, DISTANCE, TIME, CALORIES)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestFineLocation()

        createUI()

        subscribeToObservers()
    }

    private fun createUI() = with(binding){
        fabNewRun.setOnClickListener {
            findNavController().navigate(R.id.trackingFragment)
        }

        with(rvRun){
            layoutManager = LinearLayoutManager(context)
            adapter = MainRecyclerViewAdapter()
        }

        with(spinnerSort){
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                typeArray
            )
            onItemSelectedListener = myOnItemSelectedListener
        }
    }

    private val myOnItemSelectedListener = object : OnItemSelectedListener{
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            updateRecyclerView(typeArray[p2])
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    private fun subscribeToObservers(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.flowSortedRuns.collect{
                    (binding.rvRun.adapter as MainRecyclerViewAdapter)
                        .updateData(it)
                }
            }
        }
    }

    private fun updateRecyclerView(type: SortType){
        mainViewModel.getSortedRuns(type)
    }

    private fun requestLocationPermissions() {
        if(!MyPermissions.checkLocationPermissions(requireContext()))
            locationPermResult.launch(ACCESS_BACKGROUND_LOCATION)
    }

    private fun requestFineLocation() =
        if(!MyPermissions.checkFineLocationPermissions(requireContext()))
            locationFinePermResult.launch(ACCESS_FINE_LOCATION)
        else
            requestLocationPermissions()

    private val locationPermResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(!it) {
            Toast.makeText(
                requireContext(),
                TOAST_TEXT_BACKGROUND_PERM,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val locationFinePermResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it){
            requestLocationPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    /**
     * йоу йоу чувак это репчик
     */
}