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
import kotlinx.coroutines.withContext

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
        var hasTicket = false // 이용권 있는 페이지와 없는 페이지 테스트 용도
//        var hasTicket = true
        var name = ""
        CoroutineScope(Dispatchers.IO).launch {
//            hasTicket = dataStore.getHasTicket()
            name = dataStore.getUserName().toString()
            withContext(Dispatchers.Main){
                if(hasTicket) {
                    binding.ticketBack.visibility = View.VISIBLE
                    binding.ticketImg.visibility = View.VISIBLE
                    binding.ticketTitle.visibility = View.VISIBLE
                    binding.greetMsg.text =
                        String.format(mContext!!.resources.getString(R.string.home_greet, name))
                } else {
                    binding.greetMsg.text =
                        String.format(mContext!!.resources.getString(R.string.home_greet_no, name))
                    binding.nonTicketImg.visibility = View.VISIBLE
                    binding.nonTicketText.visibility = View.VISIBLE
                    binding.nonticketBack.visibility = View.VISIBLE
                    binding.nonTicketTitle.visibility = View.VISIBLE
                }
            }
        }
        binding.mainNotice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
        }
        binding.gym.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchGymFragment)
        }
        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchGymFragment)
        }

//        binding.findGymBtn.setOnClickListener {
//
//        }
//        binding.idBtn.setOnClickListener {
//            val dialog = QRCreateDialogFragment()
//            dialog.show(childFragmentManager, "QRCreateDialog")
//        }
    }
}