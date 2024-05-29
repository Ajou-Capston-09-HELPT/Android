package com.ajou.helpt.home.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentNoticeDetailBinding
import com.ajou.helpt.home.HomeInfoViewModel


class NoticeDetailFragment : Fragment() {
    private lateinit var viewModel : HomeInfoViewModel
    private var _binding : FragmentNoticeDetailBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[HomeInfoViewModel::class.java]
        _binding = FragmentNoticeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("noticedetail",viewModel.notice.value.toString())

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.noticeTitle.text = viewModel.notice.value!!.title

        binding.noticeDate.text = viewModel.notice.value!!.createAt

        binding.noticeContents.text = viewModel.notice.value!!.content

//        // 안전한 호출을 사용하여 arguments에서 공지사항 데이터를 추출합니다.
//        arguments?.let { bundle ->
//            view.findViewById<TextView>(R.id.detailTitle).text = bundle.getString("title", "")
//            view.findViewById<TextView>(R.id.detailDate).text = bundle.getString("date", "")
//            view.findViewById<TextView>(R.id.detailContent).text = bundle.getString("content", "")
//        }
    }
}
