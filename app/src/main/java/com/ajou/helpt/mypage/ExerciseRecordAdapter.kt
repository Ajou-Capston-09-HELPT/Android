package com.ajou.helpt.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.R

class ExerciseRecordAdapter(mContext: Context, emptyList: List<Any>) :
    ListAdapter<ExerciseRecord, ExerciseRecordAdapter.ExerciseRecordViewHolder>(
        ExerciseRecordDiffCallback
    ) {

    class ExerciseRecordViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val exerciseName: TextView = view.findViewById(R.id.tvExerciseRecordName)
        val exerciseSets: TextView = view.findViewById(R.id.tvExerciseRecordSetsValue)
        val exerciseReps: TextView = view.findViewById(R.id.tvExerciseRecordRepsValue)
        val exerciseTime: TextView = view.findViewById(R.id.tvExerciseRecordTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseRecordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_record, parent, false)
        return ExerciseRecordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseRecordViewHolder, position: Int) {
        val exerciseRecord = getItem(position)
        holder.exerciseName.text = exerciseRecord.exerciseName
        holder.exerciseSets.text = exerciseRecord.sets.toString()
        holder.exerciseReps.text = exerciseRecord.reps.toString()
        holder.exerciseTime.text = exerciseRecord.time
    }

    companion object ExerciseRecordDiffCallback : DiffUtil.ItemCallback<ExerciseRecord>() {
        override fun areItemsTheSame(oldItem: ExerciseRecord, newItem: ExerciseRecord): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExerciseRecord, newItem: ExerciseRecord): Boolean {
            return oldItem == newItem
        }
    }
}