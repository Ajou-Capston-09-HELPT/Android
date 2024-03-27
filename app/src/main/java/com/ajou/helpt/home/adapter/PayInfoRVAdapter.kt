package com.ajou.helpt.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.R
import com.ajou.helpt.databinding.ItemPayInfoBinding
import com.ajou.helpt.home.model.PayInfo
import java.text.DecimalFormat

class PayInfoRVAdapter(val context: Context, val list: List<PayInfo>): RecyclerView.Adapter<PayInfoRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPayInfoBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: PayInfo){
            val dec = DecimalFormat("#,###")
            binding.period.text = item.period
            binding.price.text = String.format(context.resources.getString(R.string.price,dec.format(item.price.toInt())))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayInfoRVAdapter.ViewHolder {
        val binding = ItemPayInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PayInfoRVAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}