package com.example.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningapp.database.RunDao
import com.example.runningapp.database.RunningDatabase
import com.example.runningapp.localDataSource.LocalDataSource
import com.example.runningapp.localDataSource.LocalDataSourceI
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.example.runningapp.repository.Repository
import com.example.runningapp.repository.RepositoryI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRunningDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            RunningDatabase::class.java,
            "running_database"
        ).build()

    @Provides
    fun provideRunDao(
        db: RunningDatabase
    ) = db.getRunDao()

    @Provides
    fun provideRepository(
        localDataSource: LocalDataSourceI
    ) : RepositoryI = Repository(localDataSource)

    @Provides
    fun provideLocalDataSource(
        runDao: RunDao
    ): LocalDataSourceI = LocalDataSource(runDao)

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ) = context.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        MODE_PRIVATE
    )!!

    @Provides
    fun providePersonalName(
        sharedPref: SharedPreferences
    ) = sharedPref.getString(KEY_NAME, "Error") ?: ""

    @Provides
    fun providePersonalWeight(
        sharedPref: SharedPreferences
    ) = sharedPref.getInt(KEY_WEIGHT, 70)

    @Provides
    fun provideFirstTimeToggle(
        sharedPref: SharedPreferences
    ) = sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)

}