package com.ajou.helpt.home.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentPayInfoBinding
import com.ajou.helpt.home.adapter.PayInfoRVAdapter
import com.ajou.helpt.home.model.PayInfo

class PayInfoFragment : Fragment() {
    private var _binding : FragmentPayInfoBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPayInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = listOf<PayInfo>(PayInfo("1개월","50000"),PayInfo("2개월","100000"),PayInfo("3개월","150000"))
        binding.gymRv.adapter = PayInfoRVAdapter(mContext!!,list)
        binding.gymRv.layoutManager = LinearLayoutManager(mContext)

    }
}