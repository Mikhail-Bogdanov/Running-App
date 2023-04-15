package com.example.runningapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentSetupBinding
import com.example.runningapp.viewModels.SetupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private val setupViewModel: SetupViewModel by viewModels()

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIsFirstStart(savedInstanceState)

        createUI()

    }

    private fun checkIsFirstStart(savedInstanceState: Bundle?){
        if(!setupViewModel.isFirstOpen){
            val navOption = NavOptions.Builder()
                .setPopUpTo(R.id.mainFragment, true)
                .build()
            requireView().findNavController().navigate(
                R.id.action_setupFragment_to_mainFragment,
                savedInstanceState,
                navOption
            )
        }
    }

    private fun createUI() = with(binding) {
        buttonContinue.setOnClickListener{
            writePersonalDataToSharedPref()
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean = with(binding) {
        if(
            (tvName.text!!.length in 1..20)
            &&
            (tvWeight.text!!.length in 1..3)
        ) {
            val name = tvName.text.toString()
            val weightString = tvWeight.text.toString()
            val weight: Int
            try{
                weight = Integer.parseInt(weightString)
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Fill weight field right",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            setupViewModel.saveDataToSharedPref(name, weight)
            requireView().findNavController().navigate(R.id.action_setupFragment_to_mainFragment)
            return true
        } else {
            Toast.makeText(
                requireContext(),
                "Fill all fields right",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}