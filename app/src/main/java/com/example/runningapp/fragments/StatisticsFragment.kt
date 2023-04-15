package com.example.runningapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStatisticsBinding
import com.example.runningapp.other.CustomMarkerView
import com.example.runningapp.other.Utility
import com.example.runningapp.viewModels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val statisticsViewModel: StatisticsViewModel by viewModels()

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()

        statisticsViewModel.updateFlows()

        setupBarChart()
    }

    private fun setupBarChart() = with(binding) {
        barChartStatistics.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChartStatistics.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChartStatistics.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChartStatistics.apply {
            description.text = "AVG Speed Over Time"
            legend.isEnabled = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() = with(binding) {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                statisticsViewModel.flowTotalAvgSpeed.collect{
                    val avgSpeed = (it * 10f).toInt() / 10f
                    tvTotalAvgSpeed.text = "${avgSpeed}km/h"
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                statisticsViewModel.flowTotalDistance.collect{
                    val km = it / 1000f
                    tvTotalDistance.text = "${km}km"
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                statisticsViewModel.flowTotalTimeInMillis.collect{
                    val totalTimeRun = Utility.getFormattedStopWatchTime(it)
                    tvTotalTime.text = totalTimeRun
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                statisticsViewModel.flowTotalCaloriesBurned.collect{
                    tvTotalCaloriesBurned.text = "${it}kcal"
                }
            }
        }
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                statisticsViewModel.flowRunsSortedByDate.collect{
                    val avgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeed) }
                    val barDataSet = BarDataSet(avgSpeeds, "Avg Speed Over Time").apply {
                        valueTextColor = Color.WHITE
                        color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                    }
                    barChartStatistics.data = BarData(barDataSet)
                    barChartStatistics.marker = CustomMarkerView(
                        it.reversed(),
                        requireContext(),
                        R.layout.marker_view
                    )
                    barChartStatistics.invalidate()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}