package com.ajou.helpt.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.R
import com.ajou.helpt.databinding.ItemPayInfoBinding
import com.ajou.helpt.home.model.GymProduct

class PayInfoRVAdapter(val context: Context, val list: List<GymProduct>) :
    RecyclerView.Adapter<PayInfoRVAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPayInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GymProduct) {
            binding.period.text = String.format(
                context.resources.getString(
                    R.string.home_gym_pay_title,
                    item.day
                )
            )
            binding.price.text = String.format(
                context.resources.getString(
                    R.string.home_gym_pay_price,
                    item.price
                )
            )

            binding.mPrice.text = String.format(
                context.resources.getString(
                    R.string.home_gym_pay_mprice,
                    item.price / item.day
                )
            )

            if (item.day == 1) {
                binding.mPrice.visibility = View.GONE
            }
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