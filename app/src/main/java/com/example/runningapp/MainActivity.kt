package com.example.runningapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var navController: NavController? = null
    private var bottomNavigationView: BottomNavigationView? = null

    private var navHostFragment: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navHostFragment = findViewById(R.id.nav_host_fragment)

        navigateToTrackingFragmentIfNeeded(intent)

        navHostFragment!!.findNavController()
            .addOnDestinationChangedListener{ _, destination, _ ->
                when (destination.id) {
                    R.id.mainFragment, R.id.settingsFragment, R.id.statisticsFragment ->
                        bottomNavigationView!!.visibility = View.VISIBLE
                    else -> bottomNavigationView!!.visibility = View.GONE
                }
            }

        NavigationUI.setupWithNavController(bottomNavigationView!!, navController!!)

        botNavOnItemSelect()
    }

    private fun botNavOnItemSelect(){
        bottomNavigationView!!.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.main -> {
                    navController!!.navigate(R.id.mainFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.statistics -> {
                    navController!!.navigate(R.id.statisticsFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.settings -> {
                    navController!!.navigate(R.id.settingsFragment)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    Log.d("auf", "Nothing")
                    return@setOnItemSelectedListener false
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent!!.action == ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment!!.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

}