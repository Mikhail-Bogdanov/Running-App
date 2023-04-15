package com.example.runningapp.database

import androidx.room.*


@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Update
    suspend fun updateRun(run: Run)

    @Query("SELECT AVG(avgSpeed) FROM running_table")
    suspend fun getTotalAvgSpeed(): Float

    @Query("SELECT SUM(distance) FROM running_table")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(timeInMillis) FROM running_table")
    suspend fun getTotalTimeInMillis(): Long

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    suspend fun getTotalCaloriesBurned(): Int

    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    suspend fun getAllRunsSortedByDate(): Array<Run>

    @Query("SELECT * FROM running_table ORDER BY avgSpeed DESC")
    suspend fun getAllRunsSortedBySpeed(): Array<Run>

    @Query("SELECT * FROM running_table ORDER BY distance DESC")
    suspend fun getAllRunsSortedByDistance(): Array<Run>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    suspend fun getAllRunsSortedByTimeInMillis(): Array<Run>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    suspend fun getAllRunsSortedByCaloriesBurned(): Array<Run>
    
}