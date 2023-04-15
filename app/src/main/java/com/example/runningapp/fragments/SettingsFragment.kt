package com.example.runningapp.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runningapp.databinding.FragmentSettingsBinding
import com.example.runningapp.viewModels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @set:Inject
    var userName = "Error"

    @set:Inject
    var userWeight = 70

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createUI()

        readPersonalDataFromSharedPref()
    }

    private fun createUI() = with(binding){
        buttonChange.setOnClickListener {
            writePersonalDataToSharedPref()
        }
    }

    private fun readPersonalDataFromSharedPref() = with(binding) {
        tvNameSettings.text = userName.toEditable()
        tvWeightSettings.text = userWeight.toString().toEditable()
    }

    private fun writePersonalDataToSharedPref(): Boolean = with(binding) {
        if(
            (tvNameSettings.text!!.length in 1..20)
            &&
            (tvWeightSettings.text!!.length in 1..3)
        ) {
            val name = tvNameSettings.text.toString()
            val weightString = tvWeightSettings.text.toString()
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
            settingsViewModel.saveDataToSharedPref(name, weight)
            Toast.makeText(
                requireContext(),
                "Saved successful",
                Toast.LENGTH_SHORT
            ).show()
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

    private fun String.toEditable() = Editable.Factory.getInstance().newEditable(this)
}