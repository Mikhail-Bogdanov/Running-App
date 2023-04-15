package com.example.runningapp.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
): ViewModel() {

    @set:Inject
    var isFirstOpen = true

    fun saveDataToSharedPref(
        name: String,
        weight: Int
    ) = sharedPref.edit()
            .putString(KEY_NAME, name)
            .putInt(KEY_WEIGHT, weight)
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()

}