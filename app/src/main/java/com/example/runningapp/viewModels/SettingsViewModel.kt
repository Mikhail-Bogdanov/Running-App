package com.example.runningapp.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.runningapp.other.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {

    fun saveDataToSharedPref(
        name: String,
        weight: Int
    ) = sharedPref.edit()
            .putString(Constants.KEY_NAME, name)
            .putInt(Constants.KEY_WEIGHT, weight)
            .apply()

}