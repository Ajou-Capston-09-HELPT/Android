package com.ajou.helpt.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.databinding.ItemEquipBinding

class GymDetailInfoRVAdapter(val context: Context, val list: List<String>): RecyclerView.Adapter<GymDetailInfoRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEquipBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: String){
            binding.name.text = item
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GymDetailInfoRVAdapter.ViewHolder {
        val binding = ItemEquipBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GymDetailInfoRVAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}