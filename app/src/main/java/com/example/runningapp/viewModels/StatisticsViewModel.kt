package com.example.runningapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.database.Run
import com.example.runningapp.repository.RepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: RepositoryI
    ) : ViewModel() {

    val flowTotalAvgSpeed: MutableStateFlow<Float> = MutableStateFlow(0f)
    val flowTotalDistance: MutableStateFlow<Int> = MutableStateFlow(0)
    val flowTotalTimeInMillis: MutableStateFlow<Long> = MutableStateFlow(0L)
    val flowTotalCaloriesBurned: MutableStateFlow<Int> = MutableStateFlow(0)
    val flowRunsSortedByDate: MutableStateFlow<ArrayList<Run>> = MutableStateFlow(arrayListOf())

    fun updateFlows(){
        updateFlowTotalAvgSpeed()
        updateFlowTotalDistance()
        updateFlowTotalTimeInMillis()
        updateFlowTotalCaloriesBurned()
        updateFlowRunsSortedByDate()
    }

    private fun updateFlowTotalAvgSpeed(){
        viewModelScope.launch {
            runCatching {
                repository.getTotalAvgSpeedLocal()
            }.onSuccess {
                flowTotalAvgSpeed.value = it

            }.onFailure {

            }
        }
    }

    private fun updateFlowTotalDistance(){
        viewModelScope.launch {
            runCatching {
                repository.getTotalDistanceLocal()
            }.onSuccess {
                flowTotalDistance.value = it
            }.onFailure {

            }
        }
    }

    private fun updateFlowTotalTimeInMillis(){
        viewModelScope.launch {
            runCatching {
                repository.getTotalTimeInMillisLocal()
            }.onSuccess {
                flowTotalTimeInMillis.value = it
            }.onFailure {

            }
        }
    }

    private fun updateFlowTotalCaloriesBurned(){
        viewModelScope.launch {
            runCatching {
                repository.getTotalCaloriesBurnedLocal()
            }.onSuccess {
                flowTotalCaloriesBurned.value = it
            }.onFailure {

            }
        }
    }

    private fun updateFlowRunsSortedByDate(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedByDateLocal()
            }.onSuccess {
                flowRunsSortedByDate.value = it
            }.onFailure {

            }
        }
    }
}