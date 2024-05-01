package com.ajou.helpt.auth.view.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.auth.view.UserInfoViewModel
import com.ajou.helpt.databinding.FragmentSetSexInfoBinding

class SetSexInfoFragment : Fragment() {
    private var _binding: FragmentSetSexInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel : UserInfoViewModel

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
        viewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]
        _binding = FragmentSetSexInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.manBtn.setOnClickListener {
            binding.manBtn.setImageResource(R.drawable.auth_man_on)
            binding.manText.setTextColor(ContextCompat.getColor(mContext!!, R.color.primary))
            binding.womanBtn.setImageResource(R.drawable.auth_woman_off)
            binding.womanText.setTextColor(ContextCompat.getColor(mContext!!, R.color.gray_off))
            viewModel.setSexInfo("MAN")
            binding.nextBtn.isEnabled = true
        }
        binding.womanBtn.setOnClickListener {
            binding.womanBtn.setImageResource(R.drawable.auth_woman_on)
            binding.womanText.setTextColor(ContextCompat.getColor(mContext!!, R.color.primary))
            binding.manBtn.setImageResource(R.drawable.auth_man_off)
            binding.manText.setTextColor(ContextCompat.getColor(mContext!!, R.color.gray_off))
            viewModel.setSexInfo("WOMEN")
            binding.nextBtn.isEnabled = true
        }

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setSexInfoFragment_to_setUserInfoFragment)
        }
    }
}