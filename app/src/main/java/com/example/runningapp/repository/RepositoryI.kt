package com.example.runningapp.repository


import com.example.runningapp.database.Run

interface RepositoryI {

    suspend fun insertRunLocal(run: Run)

    suspend fun deleteRunLocal(run: Run)

    suspend fun updateRunLocal(run: Run)

    suspend fun getTotalAvgSpeedLocal(): Float

    suspend fun getTotalDistanceLocal(): Int

    suspend fun getTotalTimeInMillisLocal(): Long

    suspend fun getTotalCaloriesBurnedLocal(): Int

    suspend fun getAllRunsSortedByDateLocal(): ArrayList<Run>

    suspend fun getAllRunsSortedBySpeedLocal(): ArrayList<Run>

    suspend fun getAllRunsSortedByDistanceLocal(): ArrayList<Run>

    suspend fun getAllRunsSortedByTimeInMillisLocal(): ArrayList<Run>

    suspend fun getAllRunsSortedByCaloriesBurnedLocal(): ArrayList<Run>


}