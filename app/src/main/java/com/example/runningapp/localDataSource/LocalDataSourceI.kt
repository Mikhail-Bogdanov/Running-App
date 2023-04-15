package com.example.runningapp.localDataSource

import com.example.runningapp.database.Run

interface LocalDataSourceI {

    suspend fun insertRun(run: Run)

    suspend fun deleteRun(run: Run)

    suspend fun updateRun(run: Run)

    suspend fun getTotalAvgSpeed(): Float

    suspend fun getTotalDistance(): Int

    suspend fun getTotalTimeInMillis(): Long

    suspend fun getTotalCaloriesBurned(): Int

    suspend fun getAllRunsSortedByDate(): ArrayList<Run>

    suspend fun getAllRunsSortedBySpeed(): ArrayList<Run>

    suspend fun getAllRunsSortedByDistance(): ArrayList<Run>

    suspend fun getAllRunsSortedByTimeInMillis(): ArrayList<Run>

    suspend fun getAllRunsSortedByCaloriesBurned(): ArrayList<Run>
}