package com.example.runningapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.database.Run
import com.example.runningapp.other.SortType
import com.example.runningapp.other.SortType.*
import com.example.runningapp.repository.RepositoryI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: RepositoryI) : ViewModel() {

    val flowSortedRuns: MutableStateFlow<ArrayList<Run>> = MutableStateFlow(arrayListOf())

    fun getSortedRuns(type: SortType) = when(type) {
        DATE -> getRunsSortedByDate()
        SPEED -> getRunsSortedBySpeed()
        DISTANCE -> getRunsSortedByDistance()
        TIME -> getRunsSortedByTimeInMillis()
        CALORIES -> getRunsSortedByCaloriesBurned()
    }

    private fun getRunsSortedByDate(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedByDateLocal()
            }.onSuccess {
                flowSortedRuns.value = it
            }.onFailure {

            }
        }
    }

    private fun getRunsSortedBySpeed(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedBySpeedLocal()
            }.onSuccess {
                flowSortedRuns.value = it
            }.onFailure {

            }
        }
    }

    private fun getRunsSortedByDistance(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedByDistanceLocal()
            }.onSuccess {
                flowSortedRuns.value = it
            }.onFailure {

            }
        }
    }

    private fun getRunsSortedByTimeInMillis(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedByTimeInMillisLocal()
            }.onSuccess {
                flowSortedRuns.value = it
            }.onFailure {

            }
        }
    }

    private fun getRunsSortedByCaloriesBurned(){
        viewModelScope.launch {
            runCatching {
                repository.getAllRunsSortedByCaloriesBurnedLocal()
            }.onSuccess {
                flowSortedRuns.value = it
            }.onFailure {

            }
        }
    }

}