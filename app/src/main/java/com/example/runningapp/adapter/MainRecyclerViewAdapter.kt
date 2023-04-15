package com.example.runningapp.adapter

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningapp.database.Run
import com.example.runningapp.databinding.RunItemBinding
import com.example.runningapp.other.Utility
import java.text.SimpleDateFormat
import java.util.*

class MainRecyclerViewAdapter(
    private var data: ArrayList<Run> = arrayListOf()
) : RecyclerView.Adapter<MainRecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: RunItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = RunItemBinding.
                inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val run = data[position]
        with(holder){
            with(binding){
                ivImage.setImageBitmap(run.image)
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = run.timestamp
                }
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                tvTimestamp.text = dateFormat.format(calendar.time)
                tvAvgSpeed.text = run.avgSpeed.toString() + "km/h"
                tvDistance.text = run.distance.toString() + "m"
                tvTimeInMillis.text = Utility.getFormattedStopWatchTime(run.timeInMillis, false)
                tvCaloriesBurned.text = run.caloriesBurned.toString() + " kcal"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: ArrayList<Run>){
        this.data = data
        this.notifyDataSetChanged()
    }
}