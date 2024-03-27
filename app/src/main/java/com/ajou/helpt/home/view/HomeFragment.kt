package com.ajou.helpt.home.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context?= null
    private val dataStore = UserDataStore()
    private var accessToken : String? = null

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        var hasTicket = false // 이용권 있는 페이지와 없는 페이지 테스트 용도
        var hasTicket = true
//        CoroutineScope(Dispatchers.IO).launch {
//            hasTicket = dataStore.getHasTicket()
//        }
        if (hasTicket) {
            binding.findGymBtn.visibility = View.VISIBLE
            binding.idExistBtn.visibility = View.VISIBLE
            binding.idExistText.visibility = View.VISIBLE
            binding.noticeBtn.visibility = View.VISIBLE
            binding.idBtn.visibility = View.VISIBLE
        } else {
            binding.idNotExistText.visibility = View.VISIBLE
            binding.enrollBtn.visibility = View.VISIBLE
        }

        binding.noticeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
        }
        binding.findGymBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchGymFragment)
        }
    }
}