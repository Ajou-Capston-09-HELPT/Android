package com.ajou.helpt.home.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.home.model.NoticeData
import com.ajou.helpt.R
import com.ajou.helpt.home.adapter.NoticeAdapter

class NoticeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.noticeRecyclerView)

        val notices = listOf(
            NoticeData("공지사항 1", "2024-01-01", "내용 1"),
            NoticeData("공지사항 2", "2024-01-02", "내용 2")
            // 공지 데이터 불러오기
        )

        noticeAdapter = NoticeAdapter(notices) { noticeData ->
            // Handle click event, open NoticeDetailFragment
            val bundle = Bundle().apply {
                putString("title", noticeData.title)
                putString("date", noticeData.date)
                putString("content", noticeData.content)
            }

            findNavController().navigate(R.id.action_noticeFragment_to_noticeDetailFragment, bundle)

        }

        recyclerView.adapter = noticeAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }
}
