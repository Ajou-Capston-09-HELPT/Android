package com.ajou.helpt.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.databinding.ItemSearchGymBinding
import com.ajou.helpt.home.view.SearchGymFragment
import com.ajou.helpt.home.model.Gym

class SearchGymRVAdapter(val context: Context, var list : List<Gym>, val link : SearchGymFragment.AdapterToFragment):RecyclerView.Adapter<SearchGymRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchGymBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:Gym){
            binding.name.text = item.name
            binding.address.text = item.address

            binding.item.setOnClickListener {
                link.getSelectedItem(item)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchGymBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}