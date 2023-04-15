package com.example.runningapp.repository


import com.example.runningapp.database.Run
import com.example.runningapp.localDataSource.LocalDataSourceI
import javax.inject.Inject

class Repository @Inject constructor(
    private val localDataSource: LocalDataSourceI
) : RepositoryI {
    override suspend fun insertRunLocal(run: Run) {
        return localDataSource.insertRun(run)
    }

    override suspend fun deleteRunLocal(run: Run) {
        return localDataSource.deleteRun(run)
    }

    override suspend fun updateRunLocal(run: Run) {
        return localDataSource.updateRun(run)
    }

    override suspend fun getTotalAvgSpeedLocal(): Float {
        return localDataSource.getTotalAvgSpeed()
    }

    override suspend fun getTotalDistanceLocal(): Int {
        return localDataSource.getTotalDistance()
    }

    override suspend fun getTotalTimeInMillisLocal(): Long {
        return localDataSource.getTotalTimeInMillis()
    }

    override suspend fun getTotalCaloriesBurnedLocal(): Int {
        return localDataSource.getTotalCaloriesBurned()
    }

    override suspend fun getAllRunsSortedByDateLocal(): ArrayList<Run> {
        return localDataSource.getAllRunsSortedByDate()
    }

    override suspend fun getAllRunsSortedBySpeedLocal(): ArrayList<Run> {
        return localDataSource.getAllRunsSortedBySpeed()
    }

    override suspend fun getAllRunsSortedByDistanceLocal(): ArrayList<Run> {
        return localDataSource.getAllRunsSortedByDistance()
    }

    override suspend fun getAllRunsSortedByTimeInMillisLocal(): ArrayList<Run> {
        return localDataSource.getAllRunsSortedByTimeInMillis()
    }

    override suspend fun getAllRunsSortedByCaloriesBurnedLocal(): ArrayList<Run> {
        return localDataSource.getAllRunsSortedByCaloriesBurned()
    }

}