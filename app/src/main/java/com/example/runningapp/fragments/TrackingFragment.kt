package com.example.runningapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.database.Run
import com.example.runningapp.databinding.FragmentTrackingBinding
import com.example.runningapp.events.Event
import com.example.runningapp.other.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.other.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.other.Constants.MAP_ZOOM
import com.example.runningapp.other.Constants.POLYLINE_COLOR
import com.example.runningapp.other.Constants.POLYLINE_WIDTH
import com.example.runningapp.other.Utility
import com.example.runningapp.services.Polyline
import com.example.runningapp.services.TrackingService
import com.example.runningapp.viewModels.TrackingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrackingFragment : Fragment(), MenuProvider {

    private val trackingViewModel: TrackingViewModel by viewModels()

    private var _binging: FragmentTrackingBinding? = null
    private val binding get() = _binging!!

    private var map: GoogleMap? = null

    private var curTimeInMillis = 0L

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var menu: Menu? = null

    @set:Inject
    var weight = 80

    private var startCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binging = FragmentTrackingBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createUI(savedInstanceState)

        subscribeToObservers()
        subscribeToEventFlow()

        createMenuHost()

    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for(polyline in pathPoints){
            for(pos in polyline){
                bounds.include(pos)
            }
        }

        map!!.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
            binding.mapView.width,
            binding.mapView.height,
            (binding.mapView.height * 0.05f).toInt())
        )
    }

    private fun endRunAndSaveToDb(){
        map!!.snapshot { bmp ->
            var distanceInMeters = 0
            for(polyline in pathPoints) {
                distanceInMeters += Utility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = ((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 100.0).roundToInt() / 100f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(
                bmp,
                dateTimestamp,
                avgSpeed,
                distanceInMeters,
                curTimeInMillis,
                caloriesBurned
            )
            trackingViewModel.insertRun(run)
        }
    }

    private fun subscribeToEventFlow(){
        trackingViewModel.eventsFlow.onEach {
            when(it){
                is Event.ShowSnackBar -> {

                    val text = when(it.success){
                        true -> "Run saved successfully"
                        false -> "Failed to save run"
                    }
                    Snackbar.make(
                        requireActivity().findViewById(R.id.main_activity_layout),
                        text,
                        Snackbar.LENGTH_SHORT
                    ).also { snackbar ->
                        snackbar.anchorView = requireActivity().findViewById(R.id.view_for_snackbar)
                        snackbar.show()
                    }
                    stopRun()
                }
                is Event.ShowToast -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun subscribeToObservers(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                TrackingService.isTracking.collect {
                    startCount++
                    updateTracking(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                TrackingService.pathPoints.collect {
                    if (it != null) {
                        pathPoints = it
                        addLatestPolyline()
                        moveCameraToUser()
                    }
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                TrackingService.timeRunInMillis.collect {
                    if (it != null) {
                        curTimeInMillis = it
                        val formattedTime = Utility.getFormattedStopWatchTime(curTimeInMillis, true)
                        binding.tvTimer.text = formattedTime
                    }
                }
            }
        }
    }

    private fun toggleRun(){
        if(isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking) {
            binding.bStartRunning.text = "Start"
            if(pathPoints.isNotEmpty()) {
                binding.bFinishRunning.visibility = View.VISIBLE
            }
        } else {
            binding.bStartRunning.text = "Stop"
            binding.bFinishRunning.visibility = View.GONE
        }
    }

    private fun addAllPolylines(){
        for(polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun stopRun(){
        binding.tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_mainFragment)
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also{
            it.action = action
            requireContext().startService(it)
        }

    private fun createUI(savedInstanceState: Bundle?) = with(binding){
        bStartRunning.setOnClickListener {
            toggleRun()
        }
        bFinishRunning.setOnClickListener{
            if(pathPoints.isNotEmpty()){
                zoomToSeeWholeTrack()
                endRunAndSaveToDb()
            } else {
                Snackbar.make(
                    requireView(),
                    "Error Saving Run",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            addAllPolylines()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binging = null

    }

    private fun showCancelTrackingDialog(){
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel the run?")
            .setMessage("Are you sure to cancel the current run and delete all its data?")
            .setIcon(R.drawable.ic_delete_24)
            .setPositiveButton("Yes"){ _, _ ->
                stopRun()
            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create().show()
    }

    private fun createMenuHost(){
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId){
            R.id.cancelTracking -> showCancelTrackingDialog()
        }
        return true
    }

}