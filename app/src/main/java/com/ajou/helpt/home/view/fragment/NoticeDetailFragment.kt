package com.ajou.helpt.home.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ajou.helpt.R


class NoticeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("hi","hio")
        // 안전한 호출을 사용하여 arguments에서 공지사항 데이터를 추출합니다.
        arguments?.let { bundle ->
            view.findViewById<TextView>(R.id.detailTitle).text = bundle.getString("title", "")
            view.findViewById<TextView>(R.id.detailDate).text = bundle.getString("date", "")
            view.findViewById<TextView>(R.id.detailContent).text = bundle.getString("content", "")
        }
    }
}
