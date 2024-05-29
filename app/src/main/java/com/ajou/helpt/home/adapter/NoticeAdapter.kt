package com.ajou.helpt.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.home.model.NoticeData
import com.ajou.helpt.R
import com.ajou.helpt.databinding.ItemNoticeBinding
import com.ajou.helpt.home.model.GymRegisteredInfo
import com.ajou.helpt.home.view.fragment.NoticeFragment

class NoticeAdapter(val context: Context, var list: List<NoticeData>, val link : NoticeFragment.AdapterToFragment) :
    RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeData) {
            binding.title.text = item.title
            binding.date.text = item.createAt

            binding.item.setOnClickListener {
                link.getSelectedItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }


    override fun getItemCount() = list.size

    fun updateList(newList: List<NoticeData>){
        list = newList
        notifyDataSetChanged()
    }
}
