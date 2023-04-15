package com.example.runningapp.other

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.widget.TextView
import com.example.runningapp.R
import com.example.runningapp.database.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    private val runs: List<Run>,
    c: Context,
    layoutId: Int
): MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }


    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if(e == null){
            return
        }
        val currentRunId = e.x.toInt()
        val run = runs[currentRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tv_date).text = dateFormat.format(calendar.time)
        findViewById<TextView>(R.id.tv_avg_speed).text = run.avgSpeed.toString() + "km/h"
        findViewById<TextView>(R.id.tv_distance).text = run.distance.toString() + "m"
        findViewById<TextView>(R.id.tv_duration).text = Utility.getFormattedStopWatchTime(run.timeInMillis, false)
        findViewById<TextView>(R.id.tv_caloriesBurned).text = run.caloriesBurned.toString() + " kcal"

    }
}