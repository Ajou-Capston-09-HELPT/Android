package com.ajou.helpt.train.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ajou.helpt.R
import com.ajou.helpt.auth.view.dialog.LogOutDialog
import com.ajou.helpt.auth.view.dialog.SelectBirthDialog
import com.ajou.helpt.databinding.FragmentReadyTrainBinding
import java.text.SimpleDateFormat
import java.util.*

class ReadyTrainFragment : Fragment() {
    private var _binding : FragmentReadyTrainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TrainInfoViewModel
    private var mContext : Context? = null
    private lateinit var dialog : TrainSettingDialog

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
        _binding = FragmentReadyTrainBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var setting = listOf<Int>(5,5,5) // 운동 초기값 설정

        binding.trainSetting.setOnClickListener {
            dialog = TrainSettingDialog(setting) { value ->
                setting = value
                binding.set.text = String.format(
                    resources.getString(R.string.train_setting_set),
                    value[0]
                )
                binding.weight.text = String.format(
                    resources.getString(R.string.train_setting_weight),
                    value[1]
                )
                binding.count.text = String.format(
                    resources.getString(R.string.train_setting_count),
                    value[2]
                )
            }
            dialog.show(requireActivity().supportFragmentManager, "setting")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::dialog.isInitialized) dialog.dismiss()
    }
}