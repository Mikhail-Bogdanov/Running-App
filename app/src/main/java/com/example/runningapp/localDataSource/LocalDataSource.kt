package com.example.runningapp.localDataSource

import com.example.runningapp.database.Run
import com.example.runningapp.database.RunDao
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val runDao: RunDao
) : LocalDataSourceI {

    override suspend fun insertRun(run: Run) =
        runDao.insertRun(run)

    override suspend fun deleteRun(run: Run) =
        runDao.deleteRun(run)

    override suspend fun updateRun(run: Run) =
        runDao.updateRun(run)

    override suspend fun getTotalAvgSpeed(): Float =
        runDao.getTotalAvgSpeed()

    override suspend fun getTotalDistance(): Int =
        runDao.getTotalDistance()

    override suspend fun getTotalTimeInMillis(): Long =
        runDao.getTotalTimeInMillis()

    override suspend fun getTotalCaloriesBurned(): Int =
        runDao.getTotalCaloriesBurned()

    override suspend fun getAllRunsSortedByDate(): ArrayList<Run> =
        ArrayList(runDao.getAllRunsSortedByDate().asList())

    override suspend fun getAllRunsSortedBySpeed(): ArrayList<Run> =
        ArrayList(runDao.getAllRunsSortedBySpeed().asList())

    override suspend fun getAllRunsSortedByDistance(): ArrayList<Run> =
        ArrayList(runDao.getAllRunsSortedByDistance().asList())

    override suspend fun getAllRunsSortedByTimeInMillis(): ArrayList<Run> =
        ArrayList(runDao.getAllRunsSortedByTimeInMillis().asList())

    override suspend fun getAllRunsSortedByCaloriesBurned(): ArrayList<Run> =
        ArrayList(runDao.getAllRunsSortedByCaloriesBurned().asList())

}