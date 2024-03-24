package com.ajou.helpt.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.NoticeData
import com.ajou.helpt.R

class NoticeAdapter(private val noticeList: List<NoticeData>, private val onClick: (NoticeData) -> Unit) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(notice: NoticeData, onClick: (NoticeData) -> Unit) {
            itemView.findViewById<TextView>(R.id.noticeTitle).text = notice.title
            itemView.findViewById<TextView>(R.id.noticeDate).text = notice.date
            itemView.setOnClickListener { onClick(notice) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(noticeList[position], onClick)
    }

    override fun getItemCount() = noticeList.size
}
